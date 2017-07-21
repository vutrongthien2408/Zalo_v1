package com.ptit.trongthien.zalo_v1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import com.ptit.trongthien.adapter.ChatAdapter;
import com.ptit.trongthien.model.ItemChat;
import com.ptit.trongthien.resource.MyResource;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by TrongThien on 4/23/2017.
 */
public class ChatContentActivity extends AppCompatActivity implements View.OnClickListener {
    public static boolean HAVE_NEW_MSG = false;
    private ListView lvChatContent;
    private ImageView imgSend;
    private ChatAdapter chatAdapter;
    private ArrayList<ItemChat> itemChats = new ArrayList<>();
    private MyResource myResource;
    private EditText edtChatContent;
    private Socket mSocket = LoginActivity.mSocket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_content);
        myResource = (MyResource) getApplicationContext();

        initView();
        initSocket();
        initData();
        sendAccountToSocket();
    }

    private void initData() {
    }

    private void sendAccountToSocket() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userNameSend", myResource.userName);
            jsonObject.put("userNameReceive", myResource.userNameReceiver);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("account-chat", jsonObject);
    }

    private void initSocket() {
//        mSocket.connect();
        mSocket.on("result-chat", msgReultChat);
        mSocket.on("have-new-msg", msgHaveNewMsg);
//        mSocket.on("avar", getAvar);
    }

    private void initView() {

        lvChatContent = (ListView) findViewById(R.id.lvListChatContent);
        chatAdapter = new ChatAdapter(itemChats, this);
        lvChatContent.setAdapter(chatAdapter);
        imgSend = (ImageView) findViewById(R.id.imgSend);
        edtChatContent = (EditText) findViewById(R.id.edtChatContent);
        imgSend.setOnClickListener(this);

    }

    //lang nghe tin chat tra ve
    private Emitter.Listener msgHaveNewMsg = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {

                        String userNameSend = jsonObject.getString("userNameSend");
                        String userNameReceive = jsonObject.getString("userNameReceive");
                        String chatContent = jsonObject.getString("chatContent");
                        String dateSend = jsonObject.getString("dateSend");
                        if ((myResource.userName.equals(userNameSend) && myResource.userNameReceiver.equals(userNameReceive))
                                || (myResource.userName.equals(userNameReceive) && myResource.userNameReceiver.equals(userNameSend))) {
                            ItemChat itemChat = new ItemChat(chatContent, userNameSend, userNameReceive, dateSend);
                            itemChats.add(itemChat);
                            chatAdapter.notifyDataSetChanged();
                        }
                        lvChatContent.post(new Runnable() {
                            @Override
                            public void run() {
                                lvChatContent.smoothScrollToPosition(itemChats.size() - 1);

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    // socket lang nghe ket qua chat tra ve tu server
    private Emitter.Listener msgReultChat = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject) args[0];
                    try {
                        itemChats.clear();
                        JSONArray jsonArray = json.getJSONArray("chatContents");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String userNameSend = object.getString("userNameSend");
                            String userNameReceive = object.getString("userNameReceive");
                            String chat = object.getString("chatContent");
                            String dateSend = object.getString("dateSend");
                            ItemChat itemChat = new ItemChat(chat, userNameSend, userNameReceive, dateSend);
                            itemChats.add(itemChat);

                        }
                        chatAdapter.notifyDataSetChanged();
                        //load den cuoi listview
                        lvChatContent.post(new Runnable() {
                            @Override
                            public void run() {
                                lvChatContent.smoothScrollToPosition(itemChats.size() - 1);

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    @Override
    public void onClick(View view) {
        String chatContent = edtChatContent.getText().toString();
        if (chatContent.isEmpty()) {
            return;
        }
        edtChatContent.setText("");
        //lay ngya thang hine tai
        DateFormat df = new SimpleDateFormat("HH:mm, EEE, d MMM yyyy,");
        String dateSend = df.format(Calendar.getInstance().getTime());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userNameSend", myResource.userName);
            jsonObject.put("userNameReceive", myResource.userNameReceiver);
            jsonObject.put("chatContent", chatContent);
            jsonObject.put("dateSend", dateSend);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //soket gui thong tin chat den server de xu ly
        mSocket.emit("send-chat-data", jsonObject);

    }
}
