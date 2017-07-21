package com.ptit.trongthien.zalo_v1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import java.util.Locale;

import static android.media.MediaFormat.KEY_LANGUAGE;
import static com.ptit.trongthien.zalo_v1.LoginActivity.LOGIN_ACCOUNT;

/**
 * Created by TrongThien on 7/7/2017.
 */
public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY_MY_LANGUAGE = "myLanguage";
    private RadioButton rdEnglish, rdVietnamese, rdJapanese, rdblack, rdWrite;
    private Button btnSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        initView();

    }

    private void initView() {
        rdEnglish = (RadioButton) findViewById(R.id.rdEnglish);
        rdJapanese = (RadioButton) findViewById(R.id.rdJapanese);
        rdVietnamese = (RadioButton) findViewById(R.id.rdVietnamese);
        rdblack = (RadioButton) findViewById(R.id.rdBlack);
        rdWrite = (RadioButton) findViewById(R.id.rdWrite);
        btnSave = (Button) findViewById(R.id.btnSaveSetting);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (rdEnglish.isChecked()) {
            changeLanguage("en");

            SharedPreferences preferences = getSharedPreferences(LOGIN_ACCOUNT, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_MY_LANGUAGE, "en");
            editor.commit();
        } else if (rdJapanese.isChecked()) {
            changeLanguage("ja");
            SharedPreferences preferences = getSharedPreferences(LOGIN_ACCOUNT, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_MY_LANGUAGE, "ja");
            editor.commit();
        } else {
            changeLanguage("vi");
            SharedPreferences preferences = getSharedPreferences(LOGIN_ACCOUNT, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_MY_LANGUAGE, "vi");
            editor.commit();
        }

        if (rdblack.isChecked()) {
            getApplication().setTheme(R.style.BlackTheme);
        } else {
            getApplication().setTheme(R.style.LightTheme);
        }
        Intent intent = new Intent(SettingActivity.this, Main.class);
        startActivity(intent);
        finish();
    }

    public void changeLanguage(String language) {
        Locale locale = new Locale(language);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,
                getBaseContext().getResources().getDisplayMetrics());

    }
}
