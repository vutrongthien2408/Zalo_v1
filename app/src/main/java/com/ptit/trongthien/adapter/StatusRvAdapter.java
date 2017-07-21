package com.ptit.trongthien.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.ptit.trongthien.model.ItemStatus;
import com.ptit.trongthien.model.LikeStatus;
import com.ptit.trongthien.model.ZaloAccount;
import com.ptit.trongthien.resource.MyResource;
import com.ptit.trongthien.zalo_v1.*;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by TrongThien on 4/24/2017.
 */
public class StatusRvAdapter extends RecyclerView.Adapter<StatusRvAdapter.MyViewHolder> {

    private ArrayList<ItemStatus> statuses = new ArrayList<>();
    private ArrayList<LikeStatus> likeStatuses = new ArrayList<>();
    private View view;

    public StatusRvAdapter(ArrayList<ItemStatus> statuses, ArrayList<LikeStatus> likeStatuses) {
        this.statuses = statuses;
        this.likeStatuses = likeStatuses;
    }

    private Socket mSocket = LoginActivity.mSocket;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int position) {
        final MyResource myResource = (MyResource) view.getContext().getApplicationContext();
        final ItemStatus itemStatus = statuses.get(position);
        viewHolder.tvStatus.setText(itemStatus.getStatus());
        viewHolder.tvPostDate.setText(itemStatus.getPostDate());
        viewHolder.tvUsernameStatus.setText(itemStatus.getUserName());
        String avatar = itemStatus.getAvatar();
        byte[] ava = Base64.decode(avatar, Base64.DEFAULT);
        Bitmap avar = BitmapFactory.decodeByteArray(ava, 0, ava.length);
        viewHolder.imgAvatarInStatus.setImageBitmap(avar);

        //set image in status
        String image = itemStatus.getImageStatus();
        byte[] img = Base64.decode(image, Base64.DEFAULT);
        Bitmap b = BitmapFactory.decodeByteArray(img, 0, img.length);
        viewHolder.imgStatus.setImageBitmap(b);

        viewHolder.tvLikeStatus.setText(itemStatus.getLikeStatus() + "");
        //
        for (int i = 0; i < likeStatuses.size(); i++) {
            if (itemStatus.getId() == likeStatuses.get(i).getIdStatus()) {
                viewHolder.imgLikeStatus.setVisibility(View.INVISIBLE);
                viewHolder.imgLikedStatus.setVisibility(View.VISIBLE);
                break;
            }
        }
        //xu ly comment
        viewHolder.imgCmtStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CommentActivity.class);
                intent.putExtra("id", itemStatus.getId());
                intent.putExtra("userName", itemStatus.getUserName());
                intent.putExtra("postDate", itemStatus.getPostDate());
                intent.putExtra("status", itemStatus.getStatus());
                intent.putExtra("avatar", itemStatus.getAvatar());
                intent.putExtra("imageStatus", itemStatus.getImageStatus());
                intent.putExtra("likeStatus", itemStatus.getLikeStatus());
                view.getContext().startActivity(intent);
            }
        });

        // xu ly like
        viewHolder.imgLikeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject json = new JSONObject();
                try {

                    viewHolder.tvLikeStatus.setText((itemStatus.getLikeStatus() + 1) + "");
                    viewHolder.imgLikeStatus.setVisibility(View.INVISIBLE);
                    viewHolder.imgLikedStatus.setVisibility(View.VISIBLE);
                    json.put("id", itemStatus.getId());
                    json.put("likeStatus", itemStatus.getLikeStatus());
                    json.put("userName", myResource.userName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSocket.emit("likeUpdate", json);
//                Toast.makeText(view.getContext(), "dalike", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatarInStatus;
        TextView tvUsernameStatus;
        TextView tvPostDate;
        TextView tvStatus;
        ImageView imgStatus;
        ImageView imgLikeStatus;
        ImageView imgCmtStatus;
        TextView tvLikeStatus;
        ImageView imgLikedStatus;

        public MyViewHolder(View view) {
            super(view);
            imgAvatarInStatus = (ImageView) view.findViewById(R.id.imgAvarInnewfeed);
            tvUsernameStatus = (TextView) view.findViewById(R.id.tvUsernameStatus);
            tvPostDate = (TextView) view.findViewById(R.id.tvPostDate);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            imgStatus = (ImageView) view.findViewById(R.id.imgStatus);
            imgLikeStatus = (ImageView) view.findViewById(R.id.imgLikeStatus);
            imgCmtStatus = (ImageView) view.findViewById(R.id.imgCmtStatus);
            tvLikeStatus = (TextView) view.findViewById(R.id.tvLikeStatus);
            imgLikedStatus = (ImageView) view.findViewById(R.id.imgLikedStatus);
        }
    }
}
