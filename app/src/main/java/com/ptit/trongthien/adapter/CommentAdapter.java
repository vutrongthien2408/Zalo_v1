package com.ptit.trongthien.adapter;

import android.content.Context;
import android.graphics.*;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ptit.trongthien.model.ItemComment;
import com.ptit.trongthien.zalo_v1.R;

import java.util.ArrayList;

/**
 * Created by TrongThien on 7/6/2017.
 */
public class CommentAdapter extends BaseAdapter {
    private ArrayList<ItemComment> itemComments = new ArrayList<>();
    private LayoutInflater inflater;

    public CommentAdapter(ArrayList<ItemComment> itemComments, Context context) {
        this.itemComments = itemComments;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return itemComments.size();
    }

    @Override
    public Object getItem(int i) {
        return itemComments.get(i);
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
            view = inflater.inflate(R.layout.item_comment, viewGroup, false);
            viewHolder.tvComment = (TextView) view.findViewById(R.id.tvComment);
            viewHolder.tvUserName = (TextView) view.findViewById(R.id.tvCommentName);
            viewHolder.imgAvatarUserCmt = (ImageView) view.findViewById(R.id.imgAvatarUserCmt);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tvUserName.setText(itemComments.get(i).getName());
        viewHolder.tvComment.setText(itemComments.get(i).getComment());

        String avatar = itemComments.get(i).getAvatarUserCmt();
        byte[] a = Base64.decode(avatar, Base64.DEFAULT);
        Bitmap b = BitmapFactory.decodeByteArray(a, 0, a.length);
        Bitmap c = circleBitmap(b);
        viewHolder.imgAvatarUserCmt.setImageBitmap(c);

        return view;
    }

    class ViewHolder {
        TextView tvUserName;
        TextView tvComment;
        ImageView imgAvatarUserCmt;
    }

    //velai anh thanh hinh tron
    public Bitmap circleBitmap(Bitmap b) {
        Bitmap circleBitmap = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(b, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);
        paint.setAntiAlias(true);
        Canvas c = new Canvas(circleBitmap);
        float img_w = b.getWidth();
        float img_h = b.getHeight();
        if (img_w > img_h) {
            img_w = img_h;
        }
        c.drawCircle(b.getWidth() / 2, b.getHeight() / 2, img_w / 2, paint);
        return circleBitmap;
    }
}
