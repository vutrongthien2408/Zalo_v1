package com.ptit.trongthien.zalo_v1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.*;
import android.widget.*;
import com.ptit.trongthien.adapter.StatusAdapter;
import com.ptit.trongthien.adapter.StatusRvAdapter;
import com.ptit.trongthien.model.ItemStatus;
import com.ptit.trongthien.model.LikeStatus;
import com.ptit.trongthien.resource.MyResource;
import com.ptit.trongthien.rv_click.RecyclerItemClickListener;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by TrongThien on 4/23/2017.
 */
public class NewfeedFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_IMAGE = 1;

    private static final int REQUSET_CODE = 2;
    private EditText edtStatus;
    private ImageView imgPostImage;
    private ImageView imgChooseImage;
    private ImageView imgModeStatus;
    private ImageView imgPostStatus;
    private ImageView imgCancle, imgAddPhoto;
    private TextView tvModeStatus;
    private ImageView imgIcon;

    private RecyclerView rvListStatus;
    private ArrayList<ItemStatus> statuses = new ArrayList<>();
    private ArrayList<LikeStatus> likeStatuses = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    private String imagePost = "";
    private String modeStatus;
    private PopupWindow popupWindow;
    private LayoutInflater inflater;

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
        View view = inflater.inflate(R.layout.newfeed, container, false);
        mSocket.connect();
        return view;
    }

    @Override
    public void onStart() {
        initSocket();
        initView();
        super.onStart();
    }

    private void initSocket() {
//        mSocket.connect();
        //lang nghe tat ca status tu server tra ve
        mSocket.on("status", msgLoadAllStatus);

        //lang nghe ket qua dang status tu server tra ve
        mSocket.on("have-new-status", msgHaveNewStatus);
        // yeeu cau laod tat ca status tu server
        mSocket.emit("load-all-status", "");
        //
        JSONObject jsonObject = new JSONObject();
        MyResource myResource = (MyResource) getActivity().getApplicationContext();
        try {
            jsonObject.put("userName", myResource.userName);
            mSocket.emit("like", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.on("likeUpdated", likeUpdated);
    }

    private void initView() {
        rvListStatus = (RecyclerView) getActivity().findViewById(R.id.rvListStatuses);
        layoutManager = new GridLayoutManager(getContext(), 1);
        rvListStatus.setLayoutManager(layoutManager);
        rvListStatus.setHasFixedSize(true);
        adapter = new StatusRvAdapter(statuses, likeStatuses);
        rvListStatus.setAdapter(adapter);
//        click vao tung item status
//        rvListStatus.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
//                new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        Intent intent = new Intent(getActivity(), CommentActivity.class);
//                        intent.putExtra("id",statuses.get(position).getId());
//                        intent.putExtra("userName",statuses.get(position).getUserName());
//                        intent.putExtra("postDate",statuses.get(position).getPostDate());
//                        intent.putExtra("status",statuses.get(position).getStatus());
//                        intent.putExtra("avatar",statuses.get(position).getAvatar());
//                        intent.putExtra("imageStatus",statuses.get(position).getImageStatus());
//                        intent.putExtra("likeStatus",statuses.get(position).getLikeStatus());
//                        startActivity(intent);
//                    }
//                })
//        );
//        statusAdapter = new StatusAdapter(statuses, getContext());
//        lvListStatus.setAdapter(statusAdapter);

        edtStatus = (EditText) getActivity().findViewById(R.id.edtStatus);
        imgChooseImage = (ImageView) getActivity().findViewById(R.id.imgChooseImage);
        imgModeStatus = (ImageView) getActivity().findViewById(R.id.imgModeStatus);
        imgPostStatus = (ImageView) getActivity().findViewById(R.id.imgPostStatus);
        imgCancle = (ImageView) getActivity().findViewById(R.id.imgCanclePost);
        imgPostImage = (ImageView) getActivity().findViewById(R.id.imgPostImage);
        imgIcon = (ImageView) getActivity().findViewById(R.id.imgIcon);
        tvModeStatus = (TextView) getActivity().findViewById(R.id.tvModeStatus);
        imgAddPhoto = (ImageView) getActivity().findViewById(R.id.imgAddPhoto);

//set kich thuoc cua imageView khi dang status
        setHeightForImagePost();
// set click cho cac icon tren newfeed
        imgChooseImage.setOnClickListener(this);
        imgCancle.setOnClickListener(this);
        imgPostStatus.setOnClickListener(this);
        imgIcon.setOnClickListener(this);
        imgModeStatus.setOnClickListener(this);
        imgAddPhoto.setOnClickListener(this);

// click vao tung status
//        rvListStatus.setIt(this);
    }

    private Emitter.Listener msgHaveNewStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //lay ket qua dang status tu server tra ve
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        boolean result = jsonObject.getBoolean("resultStatus");
                        String name = jsonObject.getString("userName");
                        MyResource myResource = (MyResource) getActivity().getApplicationContext();
                        if (result == true) {
                            if (name.equals(myResource.userName)) {
                                Toast.makeText(getContext(), "Dang status thanh cong", Toast.LENGTH_SHORT).show();
                                mSocket.emit("load-all-status", "");
                            } else {
                                Toast.makeText(getContext(), name + " vua cap nhap cam nghi moi", Toast.LENGTH_SHORT).show();
                                mSocket.emit("load-all-status", "");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    // lay tat ca status do len listview
    private Emitter.Listener msgLoadAllStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    statuses.clear();
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("statuses");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            int id = object.getInt("id");
                            String userName = object.getString("userName");
                            String postDate = object.getString("postDate");
                            String status = object.getString("status");
                            String avatar = object.getString("avatar");
                            String imageStatus = object.getString("imageStatus");
                            int likeStatus = object.getInt("likeStatus");
                            ItemStatus itemStatus = new ItemStatus(id, userName, postDate, status, avatar, imageStatus, likeStatus);
                            statuses.add(0, itemStatus);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    private Emitter.Listener likeUpdated = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject) args[0];
                    try {
                        JSONArray jsonArray = json.getJSONArray("like");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            int id = object.getInt("id");
                            int idStatus = object.getInt("idStatus");
                            String userName = object.getString("userName");
                            LikeStatus likeStatus = new LikeStatus(id, idStatus, userName);
                            likeStatuses.add(likeStatus);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            runnable.run();
        }

    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgChooseImage:
                Intent intentGallery = new Intent();
                intentGallery.setAction(Intent.ACTION_PICK);
                intentGallery.setType("image/*");
                startActivityForResult(intentGallery, REQUEST_IMAGE);
                break;
            case R.id.imgCanclePost:
                edtStatus.setText("");
                imagePost = "";
                setHeightForImagePost();
                break;
//                post status
            case R.id.imgPostStatus:
                String status = edtStatus.getText().toString();
                modeStatus = tvModeStatus.getText().toString();
                if (status.isEmpty() && imagePost.isEmpty()) {
                    Toast.makeText(getContext(), "Ban chua no noi dung de dang", Toast.LENGTH_SHORT).show();
                    return;
                }
                DateFormat df = new SimpleDateFormat("HH:mm, EEE, d MMM yyyy,");
                String postDate = df.format(Calendar.getInstance().getTime());
                MyResource myResource = (MyResource) getContext().getApplicationContext();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("userName", myResource.userName);
                    jsonObject.put("postDate", postDate);
                    jsonObject.put("status", status);
                    jsonObject.put("avatar", myResource.avatar);
                    jsonObject.put("imagePost", imagePost);
                    jsonObject.put("modeStatus", modeStatus);

                    mSocket.emit("send-status", jsonObject);
                    imagePost = "";
                    setHeightForImagePost();
                    edtStatus.setText("");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

//                dang icon
            case R.id.imgIcon:
//                tao poopupwindow
                inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup container = (ViewGroup) inflater.inflate(R.layout.popup_window, null);
                popupWindow = new PopupWindow(container, 2000, 1000, true);
                popupWindow.showAsDropDown(edtStatus);
                int[] idImage = {R.id.imgB, R.id.imgC, R.id.imgD, R.id.imgE,
                        R.id.imgF, R.id.imgG, R.id.imgH, R.id.imgK};
                final int[] myImages = {R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e,
                        R.drawable.f, R.drawable.g, R.drawable.h, R.drawable.k};
                ImageView image;
                for (int i = 0; i < 8; i++) {
                    image = (ImageView) container.findViewById(idImage[i]);
                    final int finalI = i;
                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bitmap icon = BitmapFactory.decodeResource(getResources(), myImages[finalI]);
                            convertImage(icon);
                            imgPostImage.setImageBitmap(icon);
                            popupWindow.dismiss();
                        }
                    });
                }
                container.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
                break;

//                che do cua status
            case R.id.imgModeStatus:
//                tao popup menu
                PopupMenu popupMenu = new PopupMenu(getContext(), imgModeStatus);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        tvModeStatus.setText(menuItem.getTitle());
                        return true;
                    }
                });
                popupMenu.show();
                break;
            case R.id.imgAddPhoto:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,REQUSET_CODE);
                break;
        }
    }

    private void setHeightForImagePost() {
        if (imagePost.equals("")) {
            int width = 0;
            int height = 0;
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width, height);
            imgPostImage.setLayoutParams(parms);
        } else {
            int width = 400;
            int height = 200;
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width, height);
            imgPostImage.setLayoutParams(parms);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();                   //duong dan hinh anh tra ve
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 3, bitmap.getHeight() / 3, true);
                    convertImage(scaled);
                    imgPostImage.setImageBitmap(scaled);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        if (requestCode == REQUSET_CODE){
            if (resultCode == Activity.RESULT_OK){
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 3, bitmap.getHeight() / 3, true);
                convertImage(scaled);
                imgPostImage.setImageBitmap(bitmap);
            }
        }
    }

    public void convertImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        imagePost = Base64.encodeToString(b, Base64.DEFAULT);
        setHeightForImagePost();
    }
}
