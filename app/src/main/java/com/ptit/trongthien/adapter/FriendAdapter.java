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
import com.ptit.trongthien.model.ZaloAccount;
import com.ptit.trongthien.zalo_v1.R;

import java.util.ArrayList;

/**
 * Created by TrongThien on 4/18/2017.
 */
public class FriendAdapter extends BaseAdapter {

    private ArrayList<ZaloAccount> zaloAccounts = new ArrayList<>();
    private LayoutInflater inflater;

    public FriendAdapter(ArrayList<ZaloAccount> zaloAccounts, Context context) {
        inflater = LayoutInflater.from(context);
        this.zaloAccounts = zaloAccounts;
    }

    @Override
    public int getCount() {
        return zaloAccounts.size();
    }

    @Override
    public Object getItem(int i) {
        return zaloAccounts.get(i);
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
            view = inflater.inflate(R.layout.item_friend, viewGroup, false);
            viewHolder.imgAvartar = (ImageView) view.findViewById(R.id.imgAvatar);
            viewHolder.tvFriendName = (TextView) view.findViewById(R.id.tvFriendName);
            viewHolder.tvFriendPhone = (TextView) view.findViewById(R.id.tvFriendPhone);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final ZaloAccount zaloAccount = zaloAccounts.get(i);
        viewHolder.tvFriendName.setText(zaloAccount.getUserName());
        viewHolder.tvFriendPhone.setText(zaloAccount.getPhoneNumber());

        String avar = zaloAccount.getAvatar();
        byte[] a = Base64.decode(avar, Base64.DEFAULT);
        Bitmap b = BitmapFactory.decodeByteArray(a, 0, a.length);
        Bitmap c = circleBitmap(b);
        viewHolder.imgAvartar.setImageBitmap(c);
        return view;
    }

    class ViewHolder {
        ImageView imgAvartar;
        TextView tvFriendName;
        TextView tvFriendPhone;
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
