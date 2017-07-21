package com.ptit.trongthien.zalo_v1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.*;
import android.icu.text.UnicodeSetSpanner;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.ptit.trongthien.resource.MyResource;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import static com.ptit.trongthien.zalo_v1.LoginActivity.LOGIN_ACCOUNT;

/**
 * Created by TrongThien on 4/23/2017.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_IMAGE = 1;
    private EditText edtUserName;
    private EditText edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private EditText edtPhoneNumber;
    private ImageView imgAvatar;
    private String avatar = "";
    private Socket mSocket = LoginActivity.mSocket;
    private String updateOrRegister = "";
    private MyResource myResource;
    private TextView tvRegisterOrUpdate;
    private TextInputLayout tlConfirmPassword;

    private String nameUpdate = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resgister);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        updateOrRegister = intent.getStringExtra("updateOrRegister");

        initSocket();
        initView();
    }

    private void initSocket() {
        mSocket.on("resultRegister", msgResultRegister);
        mSocket.on("result-update", msgResultUpdate);
    }

    private void initView() {
        tvRegisterOrUpdate = (TextView) findViewById(R.id.tvRegisterOrUpdate);
        edtUserName = (EditText) findViewById(R.id.edtUserNameRegister);
        edtPassword = (EditText) findViewById(R.id.edtPasswordRegister);
        edtConfirmPassword = (EditText) findViewById(R.id.edtConfirmPassword);
        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumberRegiser);
        imgAvatar = (ImageView) findViewById(R.id.imgAvatarRegister);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        tlConfirmPassword = (TextInputLayout) findViewById(R.id.tlConfirmPassword);
        btnRegister.setOnClickListener(this);
        imgAvatar.setOnClickListener(this);

        myResource = (MyResource) getApplicationContext();
        if (updateOrRegister.equals("update")) {
            tvRegisterOrUpdate.setText(myResource.userName + "\n" + myResource.phone);
            btnRegister.setText("Update");
            byte[] a = Base64.decode(myResource.avatar, Base64.DEFAULT);
            Bitmap b = BitmapFactory.decodeByteArray(a, 0, a.length);
            imgAvatar.setImageBitmap(b);
            edtConfirmPassword.setVisibility(View.INVISIBLE);
            edtConfirmPassword.setActivated(false);
            tlConfirmPassword.setVisibility(View.INVISIBLE);
            avatar = myResource.avatar;
        }

    }

    @Override
    public void onClick(View view) {
        String userName = edtUserName.getText().toString();
        String phoneNumber = edtPhoneNumber.getText().toString();
        String password = edtPassword.getText().toString();
        String confirmPass = edtConfirmPassword.getText().toString();
        switch (view.getId()) {
            case R.id.imgAvatarRegister:
                Intent intentGallery = new Intent();
                intentGallery.setAction(Intent.ACTION_PICK);
                intentGallery.setType("image/*");
                startActivityForResult(intentGallery, REQUEST_IMAGE);
                break;
            case R.id.btnRegister:
                if (userName.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "chua du du lieu", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (avatar.length() == 0) {
                    Toast.makeText(this, "Chua chon anh dai dien", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (phoneNumber.length() < 9 || phoneNumber.length() > 11) {
                    Toast.makeText(this, "Khong phai so dien thoai", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(this, "Mat khau phai co it nhat 6 ky tu", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!updateOrRegister.equals("update"))
                    if (!confirmPass.equals(password)) {
                        Toast.makeText(this, "Mat khau khong khop", Toast.LENGTH_SHORT).show();
                        return;
                    }
                JSONObject json = new JSONObject();
                try {
                    json.put(LoginActivity.KEY_USER_NAME, userName);
                    json.put(LoginActivity.KEY_PASSWORD, password);
                    json.put(LoginActivity.KEY_PHONE_NUMBER, phoneNumber);
                    json.put(LoginActivity.KEY_AVATAR, avatar);

                    if (!updateOrRegister.equals("update")) {
                        mSocket.emit("client-register", json);
                    } else {
                        nameUpdate = edtUserName.getText().toString();
                        json.put(LoginActivity.KEY_ID, myResource.id);
                        mSocket.emit("client-update", json);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    // lang nghe ket qua tra ve tu server
    private Emitter.Listener msgResultRegister = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    boolean result = false;
//                    Toast.makeText(RegisterActivity.this,"Dang ky that bai",Toast.LENGTH_SHORT).show();
                    try {
                        result = data.getBoolean("register");
                        if (result == true) {
                            Intent intent = new Intent();
                            intent.putExtra(LoginActivity.KEY_USER_NAME, edtUserName.getText().toString());
                            intent.putExtra(LoginActivity.KEY_PHONE_NUMBER, edtPhoneNumber.getText().toString());
                            intent.putExtra(LoginActivity.KEY_PASSWORD, edtPassword.getText().toString());
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "So dien thoai hoac ten dang hap da duoc su dung", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener msgResultUpdate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    boolean result = false;
//                    Toast.makeText(RegisterActivity.this,"Dang ky that bai",Toast.LENGTH_SHORT).show();
                    try {
                        result = object.getBoolean("update");
                        if (result == true) {
                            Toast.makeText(RegisterActivity.this, "Cap nhat thanh cong", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            SharedPreferences preferences = getSharedPreferences(LOGIN_ACCOUNT, MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(LoginActivity.KEY_USER_NAME, nameUpdate);
                            editor.commit();
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "So dien thoai hoac ten dang hap da duoc su dung", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();                   //duong dan hinh anh tra ve
                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 80, 80, true);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    scaled.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] b = baos.toByteArray();
                    avatar = Base64.encodeToString(b, Base64.DEFAULT);
                    imgAvatar.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
