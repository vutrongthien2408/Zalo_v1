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
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ptit.trongthien.model.ItemStatus;
import com.ptit.trongthien.zalo_v1.R;

import java.util.ArrayList;

/**
 * Created by TrongThien on 4/24/2017.
 */
public class MyStatusAdapter extends BaseAdapter {

    private ArrayList<ItemStatus> statuses = new ArrayList<>();
    private LayoutInflater inflater;

    public MyStatusAdapter(ArrayList<ItemStatus> statuses, Context context) {
        inflater = LayoutInflater.from(context);
        this.statuses = statuses;
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
            view = inflater.inflate(R.layout.item_my_status, viewGroup, false);
            viewHolder.tvPostDate = (TextView) view.findViewById(R.id.tvMyPostDate);
            viewHolder.tvMyStatus = (TextView) view.findViewById(R.id.tvMyStatus);
            viewHolder.imgMyImage = (ImageView) view.findViewById(R.id.imgMyImage);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final ItemStatus itemStatus = statuses.get(i);
        viewHolder.tvPostDate.setText(itemStatus.getPostDate());
        viewHolder.tvMyStatus.setText(itemStatus.getStatus());
        String image = itemStatus.getImageStatus();
        byte[] a = Base64.decode(image, Base64.DEFAULT);
        Bitmap b = BitmapFactory.decodeByteArray(a, 0, a.length);
        viewHolder.imgMyImage.setImageBitmap(b);
        return view;
    }

    class ViewHolder {
        TextView tvPostDate;
        TextView tvMyStatus;
        ImageView imgMyImage;
    }
}
