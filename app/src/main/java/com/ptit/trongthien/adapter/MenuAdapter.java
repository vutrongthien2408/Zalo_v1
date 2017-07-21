package com.ptit.trongthien.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ptit.trongthien.model.ItemComment;
import com.ptit.trongthien.model.ItemMenu;
import com.ptit.trongthien.zalo_v1.R;

import java.util.ArrayList;

/**
 * Created by TrongThien on 7/6/2017.
 */
public class MenuAdapter extends BaseAdapter {
    private ArrayList<ItemMenu> itemMenus = new ArrayList<>();
    private LayoutInflater inflater;

    private TextView tvMenu;
    private ImageView img;

    public MenuAdapter(ArrayList<ItemMenu> itemMenus, Context context) {
        this.itemMenus = itemMenus;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return itemMenus.size();
    }

    @Override
    public Object getItem(int i) {
        return itemMenus.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.item_nav,viewGroup,false);
        tvMenu = (TextView) view.findViewById(R.id.tvMenu);
        img = (ImageView) view.findViewById(R.id.imgMenu);
        tvMenu.setText(itemMenus.get(i).getMenu());
        img.setImageResource(itemMenus.get(i).getImg());
        img.setBackgroundResource(itemMenus.get(i).getBackground());
        return view;
    }
}
