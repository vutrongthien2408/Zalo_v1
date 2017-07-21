package com.ptit.trongthien.zalo_v1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.ptit.trongthien.adapter.MyStatusRvAdapter;
import com.ptit.trongthien.model.ItemStatus;
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
public class PersonalPageFragment extends Fragment {

//    private ListView lvListMyStatus;
    private RecyclerView rvListMyStatus;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<ItemStatus> statuses = new ArrayList<>();
//    private MyStatusAdapter myStatusAdapter;
//    private MyStatusRvAdapter recycleStatusAdapter;
    private ImageView imgMyAvatar;

    private Socket mSocket;

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
        View root = inflater.inflate(R.layout.mystatus, container, false);
        return root;
    }

    @Override
    public void onStart() {
        initView();
        initSocket();
        super.onStart();
    }


    private void initSocket() {
        mSocket.connect();
        MyResource myResource = (MyResource) getActivity().getApplicationContext();
        mSocket.emit("load-my-status", myResource.userName);
        mSocket.on("my-status", msgMyStatus);
    }

    private void initView() {
//init recycleview
        rvListMyStatus = (RecyclerView) getActivity().findViewById(R.id.rvListStatus);
        layoutManager = new GridLayoutManager(getContext(),1);
        rvListMyStatus.setLayoutManager(layoutManager);
        rvListMyStatus.setHasFixedSize(true);
        adapter = new MyStatusRvAdapter(statuses);
        rvListMyStatus.setAdapter(adapter);
//        rvListMyStatus.addOnItemTouchListener(this);
//        lvListMyStatus = (ListView) getActivity().findViewById(R.id.lvListMyStatus);
//        myStatusAdapter = new MyStatusAdapter(statuses, getContext());
//        lvListMyStatus.setAdapter(myStatusAdapter);
        imgMyAvatar = (ImageView) getActivity().findViewById(R.id.imgMyAvatar);
        MyResource myResource = (MyResource) getActivity().getApplicationContext();
        String myAvatar = myResource.avatar;
        byte[] a = Base64.decode(myAvatar, Base64.DEFAULT);
        Bitmap b = BitmapFactory.decodeByteArray(a, 0, a.length);
        imgMyAvatar.setImageBitmap(b);
    }

    private Emitter.Listener msgMyStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        statuses.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("myStatus");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            int id = object.getInt("id");
                            String date = object.getString("postDate");
                            String status = object.getString("status");
                            String image = object.getString("imageStatus");
                            int likeStatus = object.getInt("likeStatus");
                            ItemStatus itemStatus = new ItemStatus(id,"", date, status, "", image,likeStatus);
                            statuses.add(itemStatus);
//                            myStatusAdapter.notifyDataSetChanged();
                            adapter.notifyDataSetChanged();
//                            recycleStatusAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


}
