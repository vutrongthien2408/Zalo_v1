package com.ptit.trongthien.zalo_v1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.ptit.trongthien.adapter.CommentAdapter;
import com.ptit.trongthien.model.ItemComment;
import com.ptit.trongthien.model.ItemStatus;
import com.ptit.trongthien.resource.MyResource;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by TrongThien on 6/24/2017.
 */
public class CommentActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView lvListComment;
    private TextView tvUsernameStatus, tvStatus;
    private ImageView imgAvarInStatus;
    private ImageView imgStatus;
    private ItemStatus itemStatus;
    private EditText edtComment;
    private ImageView imgSendComment;
    private TextView tvSoLike;

    private ArrayList<ItemComment> itemComments = new ArrayList<>();
    private CommentAdapter commentAdapter;

    private Socket mSocket = LoginActivity.mSocket;
    private int idStatus = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_status);
        initView();
        initData();
        initSocket();

    }


    private void initSocket() {
        mSocket.on("all-comment", loadAllComment);
        mSocket.on("have-new-comment", haveNewComment);
        mSocket.emit("load-all-comment", idStatus);
    }

    // tai du lieu
    private void initData() {
        Intent intent = getIntent();
        idStatus = intent.getIntExtra("id", 0);
        String userName = intent.getStringExtra("userName");
        String postDate = intent.getStringExtra("postDate");
        String status = intent.getStringExtra("status");
        String avatar = intent.getStringExtra("avatar");
        String imageStatus = intent.getStringExtra("imageStatus");
        int likeStatus = intent.getIntExtra("likeStatus", 0);
        itemStatus = new ItemStatus(idStatus, userName, postDate, status, avatar, imageStatus, likeStatus);

        tvStatus.setText(itemStatus.getStatus());
        tvUsernameStatus.setText(itemStatus.getUserName());
        tvSoLike.setText(itemStatus.getLikeStatus() + " liked");
        byte[] ava = Base64.decode(avatar, Base64.DEFAULT);
        Bitmap avar = BitmapFactory.decodeByteArray(ava, 0, ava.length);
        imgAvarInStatus.setImageBitmap(avar);

        byte[] img = Base64.decode(imageStatus, Base64.DEFAULT);
        Bitmap imgS = BitmapFactory.decodeByteArray(img, 0, img.length);
        imgStatus.setImageBitmap(imgS);
    }

    // anh xa
    private void initView() {
        lvListComment = (ListView) findViewById(R.id.lvListComment);
        tvStatus = (TextView) findViewById(R.id.tvStatusInCmt);
        tvUsernameStatus = (TextView) findViewById(R.id.tvUsernameStatusInCmt);
        imgAvarInStatus = (ImageView) findViewById(R.id.imgAvarInCmt);
        imgStatus = (ImageView) findViewById(R.id.imgStatusInCmt);
        edtComment = (EditText) findViewById(R.id.edtComment);
        imgSendComment = (ImageView) findViewById(R.id.imgSendComment);
        tvSoLike = (TextView) findViewById(R.id.tvSoLike);
        imgSendComment.setOnClickListener(this);

        commentAdapter = new CommentAdapter(itemComments, this);
        lvListComment.setAdapter(commentAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    // click nut cmt
    @Override
    public void onClick(View view) {
        String comment = edtComment.getText().toString();
        if (comment.equals("")) {
            return;
        }
        JSONObject json = new JSONObject();
        MyResource resource = (MyResource) getApplicationContext();
        try {
            json.put("idStatus", itemStatus.getId());
            json.put("name", resource.userName);
            json.put("comment", comment);
            json.put("avatarUserCmt", resource.avatar);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        edtComment.setText("");
        mSocket.emit("new_comment", json);
    }
// cap nhat cmt moi
    private Emitter.Listener haveNewComment = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        int idStatus = jsonObject.getInt("idStatus");
                        String name = jsonObject.getString("name");
                        String comment = jsonObject.getString("comment");
                        String avatarUserCmt = jsonObject.getString("avatarUserCmt");
                        ItemComment itemComment = new ItemComment(0, idStatus, name, comment, avatarUserCmt);
                        itemComments.add(itemComment);
                        commentAdapter.notifyDataSetChanged();
                        lvListComment.post(new Runnable() {
                            @Override
                            public void run() {
                                lvListComment.smoothScrollToPosition(itemComments.size() - 1);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    //load tat ca cmt
    private Emitter.Listener loadAllComment = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("comment");
                        itemComments.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            int id = object.getInt("id");
                            int idStatus = object.getInt("idStatus");
                            String name = object.getString("name");
                            String comment = object.getString("comment");
                            String avatarUserCmt = object.getString("avatarUserCmt");
                            ItemComment itemComment = new ItemComment(id, idStatus, name, comment, avatarUserCmt);
                            itemComments.add(itemComment);
                            commentAdapter.notifyDataSetChanged();
                            lvListComment.post(new Runnable() {
                                @Override
                                public void run() {
                                    lvListComment.smoothScrollToPosition(itemComments.size() - 1);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
