package com.ptit.trongthien.zalo_v1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.ptit.trongthien.adapter.FriendAdapter;
import com.ptit.trongthien.model.ZaloAccount;
import com.ptit.trongthien.resource.MyResource;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by TrongThien on 4/23/2017.
 */
public class ListFriendFragment extends Fragment implements AdapterView.OnItemClickListener, MenuItem.OnMenuItemClickListener {
    private ListView lvListFriend;
    private ArrayList<ZaloAccount> zaloAccounts = new ArrayList<>();
    private FriendAdapter friendAdapter;
    private MyResource myResource;

    private MenuItem mnUsername;
    private MenuItem mnAvatar;
    private MenuItem mnLogout;

    //    private Socket mSocket = LoginActivity.mSocket;
    public static Socket mSocket;

    {
        try {
            mSocket = IO.socket(Main.MY_IP);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.list_friends, container, false);
        return root;
    }


    @Override
    public void onStart() {
        initSocket();
        initView();

        super.onStart();
    }

    private void initSocket() {
        mSocket.connect();
        mSocket.emit("load-all-friend", "");
        mSocket.on("list-friend", msgListFriends);
    }

    private void initView() {
        lvListFriend = (ListView) getActivity().findViewById(R.id.lvListFriends);
        friendAdapter = new FriendAdapter(zaloAccounts, getContext());
        lvListFriend.setAdapter(friendAdapter);
        lvListFriend.setOnItemClickListener(this);

    }

    // socket lang nghe server tra ve danh sach banbe
    private Emitter.Listener msgListFriends = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject) args[0];
                    try {
                        JSONArray jsonArray = json.getJSONArray("listFriends");
                        zaloAccounts.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String name = object.getString("userName");
                            String phone = object.getString("phoneNumber");
                            String pass = object.getString("passWord");
                            String avatar = object.getString("avatar");
                            ZaloAccount account = new ZaloAccount(name, pass, phone, avatar);
                            zaloAccounts.add(account);
                            friendAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    //chuyen sang trang nhan tin rieeng
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), ChatContentActivity.class);

        MyResource myResource = (MyResource) getActivity().getApplicationContext();
        myResource.avatarReceiver = zaloAccounts.get(i).getAvatar();
        myResource.userNameReceiver = zaloAccounts.get(i).getUserName();
        myResource.phoneRecieve = zaloAccounts.get(i).getPhoneNumber();
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuInflater mnInflater = getActivity().getMenuInflater();
        mnInflater.inflate(R.menu.menu_login, menu);
        mnUsername = menu.findItem(R.id.mnNameLogin);
        mnLogout = menu.findItem(R.id.mnLogout);
        mnAvatar = menu.findItem(R.id.mnAvatar);
        mnUsername.setTitle(myResource.userName);
        mnLogout.setOnMenuItemClickListener(this);
//        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.mnLogout:
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                myResource.userName = "";
                myResource.avatarReceiver = "";
                myResource.avatar = "";
                myResource.password = "";
                myResource.phone = "";
                myResource.userNameReceiver = "";
                getActivity().finish();
                break;
        }
        return false;
    }
}
