package com.nowitest.activity;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Window;
import android.view.WindowManager;

import com.nowitest.R;
import com.nowitest.util.TypefaceSpan;

/**
 * Created by android on 19/5/16.
 */
public class ParentActivity extends AppCompatActivity
{
    SharedPreferences sharedPreferencesRemember;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferencesRemember = PreferenceManager.getDefaultSharedPreferences(ParentActivity.this);
    }

    public void setActionBarCustom(String title)
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            actionBar.setBackgroundDrawable((ContextCompat.getDrawable(getApplicationContext(), R.color.actionBar)));
        } else {

            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.actionBar));
        }

        Spannable text = new SpannableString(title);
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        text.setSpan(new TypefaceSpan(getApplicationContext(), getResources().getString(R.string.opensans_semibold)), 0, text.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar.setTitle(text);
    }

    public void setActionBarCustomWithoutBack(String title)
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setElevation(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            actionBar.setBackgroundDrawable((ContextCompat.getDrawable(getApplicationContext(), R.color.actionBar)));
        } else {

            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.actionBar));
        }

        Spannable text = new SpannableString(title);
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        text.setSpan(new TypefaceSpan(getApplicationContext(), getResources().getString(R.string.opensans_semibold)), 0, text.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar.setTitle(text);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setStatusBar()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor((ContextCompat.getColor(getApplicationContext(), R.color.actionBar)));
            } else {

                window.setStatusBarColor(getResources().getColor(R.color.actionBar));
            }
        }
    }


}
