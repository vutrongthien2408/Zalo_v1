package com.ptit.trongthien.zalo_v1;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.ptit.trongthien.resource.MyResource;

import com.ptit.trongthien.service.ZaloService;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Locale;

/**
 * Created by TrongThien on 4/23/2017.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_PASSWORD = "passWord";
    public static final String KEY_AVATAR = "avatar";
    public static final String KEY_ID = "id";

    private static final int REQUEST_CODE = 1;
    public static final String LOGIN_ACCOUNT = "loginAccount";
    private static final int REQUEST_PERMISSION = 3;
    private EditText edtUserName;
    private EditText edtPassword;
    private Button btnLogin;
    private TextView tvRegister;


    public static Socket mSocket;

    {
        try {
            mSocket = IO.socket(Main.MY_IP);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};
        if (chekPermission(permissions) == false) {
            initSocket();
            initData();
            initView();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        initService();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // ket qua cho phep hay tu choi cac quyen
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                finish();
                return;
            }
        }
        // neu nguoi dung dong ys het cac quyen
        initSocket();
        initData();
        initView();
    }

    private boolean chekPermission(String[] permissions) {

        //ktra sdk co lown hon 23 ko
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            for (int i = 0; i < permissions.length; i++) {
                int permissionState = checkSelfPermission(permissions[i]);
                //ktra xem quyen da dc cap chua
                if (permissionState != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, REQUEST_PERMISSION);
                    return true;
                }
            }
        }
        return false;
    }


    private void initService() {
        Intent intent = new Intent(this, ZaloService.class);
        startService(intent);
    }

    private void initData() {
        SharedPreferences preferences = getSharedPreferences(LOGIN_ACCOUNT, MODE_PRIVATE);
        checkLanguage(preferences.getString(SettingActivity.KEY_MY_LANGUAGE, "en"));

        JSONObject json = new JSONObject();
        try {
            json.put(KEY_USER_NAME, preferences.getString(KEY_USER_NAME, ""));
            json.put(KEY_PASSWORD, preferences.getString(KEY_PASSWORD, ""));
            mSocket.emit("client-login", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initSocket() {
        mSocket.connect();
        mSocket.on("accountLogin", saveLoginToResource);
        mSocket.on("resultLogin", mgsResultLogin);

    }

    private void initView() {
        edtUserName = (EditText) findViewById(R.id.edtUserNameLogin);
        edtPassword = (EditText) findViewById(R.id.edtPassworLogin);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvRegister = (TextView) findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvRegister:
                Intent intent = new Intent(this, RegisterActivity.class);
                intent.putExtra("updateOrRegister", "register");
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btnLogin:
                JSONObject json = new JSONObject();
                String name = edtUserName.getText().toString();
                String pass = edtPassword.getText().toString();
                if (name.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(this, "Chua nhap ten hoac mat khau", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    json.put(KEY_USER_NAME, name);
                    json.put(KEY_PASSWORD, pass);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSocket.emit("client-login", json);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                String name = data.getStringExtra(KEY_USER_NAME);
                String phone = data.getStringExtra(KEY_PHONE_NUMBER);
                String pass = data.getStringExtra(KEY_PASSWORD);
                edtUserName.setText(name);
                edtPassword.setText(pass);

            } else {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Emitter.Listener saveLoginToResource = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("userName");
                        String phone = jsonObject.getString("phoneNumber");
                        String avatar = jsonObject.getString("avatar");
                        String pass = jsonObject.getString("passWord");
                        //luu vao Myresource
                        MyResource myResource = (MyResource) getApplicationContext();
                        myResource.userName = name;
                        myResource.phone = phone;
                        myResource.avatar = avatar;
                        myResource.password = pass;
                        myResource.id = id;
                        //luu vao sharepreferrent
                        SharedPreferences preferences = getSharedPreferences(LOGIN_ACCOUNT, MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(KEY_USER_NAME, name);
                        editor.putString(KEY_PASSWORD, pass);
                        editor.putInt(KEY_ID, id);
                        editor.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener mgsResultLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject) args[0];
                    try {
                        boolean result = json.getBoolean("login");
                        if (result == true) {
                            initService();
                            Intent in = new Intent(LoginActivity.this, Main.class);
                            startActivity(in);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Ten hoac mat khau ko chinh xac", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    public void checkLanguage(String language) {
        Locale locale = new Locale(language);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,
                getBaseContext().getResources().getDisplayMetrics());

    }
}
