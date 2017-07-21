package com.ptit.trongthien.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ptit.trongthien.model.ItemStatus;
import com.ptit.trongthien.zalo_v1.R;

import java.util.ArrayList;

/**
 * Created by TrongThien on 4/22/2017.
 */
public class StatusAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<ItemStatus> statuses = new ArrayList<>();

    public StatusAdapter(ArrayList<ItemStatus> statuses, Context context) {
        this.statuses = statuses;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return statuses.size();
    }

    @Override
    public Object getItem(int i) {
        return statuses.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.item_status, viewGroup, false);
            viewHolder.imgAvatarInStatus = (ImageView) view.findViewById(R.id.imgAvarInnewfeed);
            viewHolder.tvUsernameStatus = (TextView) view.findViewById(R.id.tvUsernameStatus);
            viewHolder.tvPostDate = (TextView) view.findViewById(R.id.tvPostDate);
            viewHolder.tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            viewHolder.imgStatus = (ImageView) view.findViewById(R.id.imgStatus);

            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        final ItemStatus itemStatus = statuses.get(i);
        viewHolder.tvStatus.setText(itemStatus.getStatus());
        viewHolder.tvPostDate.setText(itemStatus.getPostDate());
        viewHolder.tvUsernameStatus.setText(itemStatus.getUserName());

        //set avar cho nguoi dang o status
        String avatar = itemStatus.getAvatar();
        byte[] ava = Base64.decode(avatar, Base64.DEFAULT);
        Bitmap avar = BitmapFactory.decodeByteArray(ava, 0, ava.length);
        viewHolder.imgAvatarInStatus.setImageBitmap(avar);

        //set image in status
        String image = itemStatus.getImageStatus();
        byte[] img = Base64.decode(image, Base64.DEFAULT);
        Bitmap b = BitmapFactory.decodeByteArray(img, 0, img.length);
        viewHolder.imgStatus.setImageBitmap(b);
        return view;
    }


    class ViewHolder {
        ImageView imgAvatarInStatus;
        TextView tvUsernameStatus;
        TextView tvPostDate;
        TextView tvStatus;
        ImageView imgStatus;
    }
}
