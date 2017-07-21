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
import com.ptit.trongthien.model.ItemChat;
import com.ptit.trongthien.resource.MyResource;
import com.ptit.trongthien.zalo_v1.R;

import java.util.ArrayList;

/**
 * Created by TrongThien on 4/18/2017.
 */
public class ChatAdapter extends BaseAdapter {
    private ArrayList<ItemChat> itemChats = new ArrayList<>();
    private LayoutInflater inflater;
    private View[] v;
//    private String avatar, avatarReceiver;
//    private String userName;

    public ChatAdapter(ArrayList<ItemChat> itemChats, Context context) {
        inflater = LayoutInflater.from(context);
        this.itemChats = itemChats;

//        this.avatar = avatar;
//        this.avatarReceiver = avatarReciever;
//        this.userName = userName;
    }

    @Override
    public int getCount() {
        v = new View[itemChats.size()];
        return itemChats.size();
    }

    @Override
    public Object getItem(int i) {
        return itemChats.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (v[i] != null) {
            return v[i];
        }
        MyResource myResource = (MyResource) inflater.getContext().getApplicationContext();
        String userNameSend = myResource.userName;
        ViewHolder viewHolder;
        final ItemChat itemChat = itemChats.get(i);
        String nameSender = itemChat.getUserNameSend();
        //if (view == null) {
        viewHolder = new ViewHolder();
        if (userNameSend.equals(nameSender)) {

            view = inflater.inflate(R.layout.item_content_right, viewGroup, false);
            viewHolder.imgAvatar = (ImageView) view.findViewById(R.id.imgAvatarSend);
            viewHolder.tvChatContent = (TextView) view.findViewById(R.id.tvContent);
            viewHolder.tvDateSend = (TextView) view.findViewById(R.id.tvDateSend);
            view.setTag(viewHolder);
        } else {
            view = inflater.inflate(R.layout.item_content_left, viewGroup, false);
            viewHolder.imgAvatar = (ImageView) view.findViewById(R.id.imgAvatarSendLeft);
            viewHolder.tvChatContent = (TextView) view.findViewById(R.id.tvContentLeft);
            viewHolder.tvDateSend = (TextView) view.findViewById(R.id.tvDateSendLeft);
            view.setTag(viewHolder);
        }
//       }else{
//           viewHolder = (ViewHolder) view.getTag();
//       }

        viewHolder.tvChatContent.setText(itemChat.getContent());
        viewHolder.tvDateSend.setText(itemChat.getDate());
        if (userNameSend.equals(nameSender)) {
            byte[] a = Base64.decode(myResource.avatar, Base64.DEFAULT);
            Bitmap b = BitmapFactory.decodeByteArray(a, 0, a.length);

            //ve lai anh thanh hinh tron
            Bitmap c = circleBitmap(b);
            viewHolder.imgAvatar.setImageBitmap(c);
        } else {
            byte[] a = Base64.decode(myResource.avatarReceiver, Base64.DEFAULT);
            Bitmap b = BitmapFactory.decodeByteArray(a, 0, a.length);

            //ve lai anh thanh hinh tron
            Bitmap c = circleBitmap(b);
            viewHolder.imgAvatar.setImageBitmap(c);
        }
        return v[i] = view;
    }

    class ViewHolder {
        ImageView imgAvatar;
        TextView tvChatContent;
        TextView tvDateSend;
    }

    //velai anh thanh hinh tron
    public Bitmap circleBitmap(Bitmap b){
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
