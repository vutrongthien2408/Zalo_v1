package com.ptit.trongthien.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ptit.trongthien.model.ItemStatus;
import com.ptit.trongthien.zalo_v1.R;

import java.util.ArrayList;

/**
 * Created by TrongThien on 4/24/2017.
 */
public class MyStatusRvAdapter extends RecyclerView.Adapter<MyStatusRvAdapter.MyViewHolder> {
    private ArrayList<ItemStatus> statuses = new ArrayList<>();

    public MyStatusRvAdapter(ArrayList<ItemStatus> statuses) {
        this.statuses = statuses;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_status, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        final ItemStatus itemStatus = statuses.get(position);
        viewHolder.tvPostDate.setText(itemStatus.getPostDate());
        viewHolder.tvMyStatus.setText(itemStatus.getStatus());
        String image = itemStatus.getImageStatus();
        byte[] a = Base64.decode(image, Base64.DEFAULT);
        Bitmap b = BitmapFactory.decodeByteArray(a, 0, a.length);
        viewHolder.imgMyImage.setImageBitmap(b);
    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvPostDate;
        TextView tvMyStatus;
        ImageView imgMyImage;

        public MyViewHolder(View view) {
            super(view);
            tvPostDate = (TextView) view.findViewById(R.id.tvMyPostDate);
            tvMyStatus = (TextView) view.findViewById(R.id.tvMyStatus);
            imgMyImage = (ImageView) view.findViewById(R.id.imgMyImage);
        }
    }
}
