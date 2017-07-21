package com.ptit.trongthien.zalo_v1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.*;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.ptit.trongthien.adapter.MenuAdapter;
import com.ptit.trongthien.model.ItemMenu;
import com.ptit.trongthien.resource.MyResource;

import java.util.ArrayList;

/**
 * Created by TrongThien on 4/23/2017.
 */
public class Main extends AppCompatActivity implements AdapterView.OnItemClickListener, LocationListener {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    public static final String MY_IP = "http://192.168.2.26:3000";
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private LinearLayout menuLayout;
    private ListView lvListMenu;
    private ImageView imgAvatarInNav;
    private TextView tvUserNameInNav;

    private MenuAdapter menuAdapter;
    private ArrayList<ItemMenu> itemMenus = new ArrayList<>();
    private int longitude, latitude;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TextView tvNewfeed = new TextView(this);
        tvNewfeed.setText(R.string.newfeed);
        String newfeed = tvNewfeed.getText().toString();
        tvNewfeed.setText(R.string.contact);
        String contact = tvNewfeed.getText().toString();
        MyResource myResource = (MyResource) getApplicationContext();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFragment(new NewfeedFragment(), newfeed);
        mSectionsPagerAdapter.addFragment(new ListFriendFragment(), contact);
        mSectionsPagerAdapter.addFragment(new PersonalPageFragment(), myResource.userName);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        //
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initNav();

    }

    private void initNav() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
        drawerLayout.addDrawerListener(toggle);
        menuLayout = (LinearLayout) this.findViewById(R.id.menuLayout);
        lvListMenu = (ListView) this.findViewById(R.id.lvListMenu);
        initData();
        menuAdapter = new MenuAdapter(itemMenus, this);
        lvListMenu.setAdapter(menuAdapter);
        lvListMenu.setOnItemClickListener(this);

        MyResource myResource = (MyResource) getApplicationContext();
        imgAvatarInNav = (ImageView) findViewById(R.id.imgAvarInNav);
        tvUserNameInNav = (TextView) findViewById(R.id.tvUserNameInNav);
        tvUserNameInNav.setText(myResource.userName);
        String avatar = myResource.avatar;
        byte[] a = Base64.decode(avatar, Base64.DEFAULT);
        Bitmap b = BitmapFactory.decodeByteArray(a, 0, a.length);
        Bitmap c = circleBitmap(b);
        imgAvatarInNav.setImageBitmap(c);

    }

    private void initData() {
        itemMenus.add(new ItemMenu(R.string.updateInfo, R.mipmap.ic_info_outline_white_24dp, R.drawable.oval1));
        itemMenus.add(new ItemMenu(R.string.chatRoom, R.mipmap.ic_group_add_white_24dp, R.drawable.oval2));
        itemMenus.add(new ItemMenu(R.string.qrCode, R.mipmap.ic_center_focus_strong_white_24dp, R.drawable.oval3));
        itemMenus.add(new ItemMenu(R.string.shop, R.mipmap.ic_add_shopping_cart_white_24dp, R.drawable.oval4));
        itemMenus.add(new ItemMenu(R.string.sticker, R.mipmap.ic_tag_faces_white_24dp, R.drawable.oval5));
        itemMenus.add(new ItemMenu(R.string.dinner, R.mipmap.ic_local_dining_white_24dp, R.drawable.oval2));
        itemMenus.add(new ItemMenu(R.string.setting, R.mipmap.ic_settings_white_24dp, R.drawable.oval1));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (toggle.onOptionsItemSelected(item)) {
                    return true;
                }
                break;
            case R.id.action_settings:
                break;
            case R.id.action_logout:
                MyResource myResource = (MyResource) getApplicationContext();
                myResource.id = 0;
                myResource.password = "";
                myResource.userName = "";
                myResource.userNameReceiver = "";
                myResource.avatar = "";
                myResource.avatarReceiver = "";
                myResource.phone = "";
                myResource.image = "";
                SharedPreferences preferences = getSharedPreferences(LoginActivity.LOGIN_ACCOUNT, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(LoginActivity.KEY_USER_NAME, "");
                editor.putString(LoginActivity.KEY_PASSWORD, "");
                editor.putInt(LoginActivity.KEY_ID, 0);
                editor.commit();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0) {
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra("updateOrRegister", "update");
            startActivity(intent);
        } else if (i == 2) {
            try {

                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

                startActivityForResult(intent, 0);

            } catch (Exception e) {

                Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                startActivity(marketIntent);
            }
        } else if (i == 5) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Main.this);
            // Create a Uri from an intent string. Use the result to create an Intent.
            Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+longitude+","+latitude+"");
// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
// Make the Intent explicit by setting the Google Maps package
            mapIntent.setPackage("com.google.android.apps.maps");
// Attempt to start an activity that can handle the Intent
            startActivity(mapIntent);
        } else if (i == 6) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = (int) (location.getLatitude());
        longitude = (int) (location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments = new ArrayList<>();
        private ArrayList<String> titles = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {

            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return titles.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
            }
            if (resultCode == RESULT_CANCELED) {
                //handle cancel
            }
        }
    }
}
