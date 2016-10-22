package com.nowitest.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.nowitest.R;
import com.nowitest.util.Constants;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;

public class FullScreenImageActivity extends ParentActivity implements View.OnClickListener
{
    Bitmap bitmap;
    ProgressDialog pDialog;

    Context context = FullScreenImageActivity.this;

    String ImageUrl;
    private ScaleGestureDetector scaleGestureDetector;
    com.nowitest.activity.ZoomableImageView image;

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }

    private void SetUpActionBar()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.actionBar));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.actionBar));
        }

//        SetUpActionBar();
        setActionBarCustom(getResources().getString(R.string.app_name));

        image = (ZoomableImageView) findViewById(R.id.Image);

        Intent intent = getIntent();
        String imagePath = intent.getStringExtra(Constants.imagepath);
        if (imagePath.equalsIgnoreCase(""))
        {
        }
        else {
            System.out.println("Image path****: " + imagePath);
            final String questionImagePath = Constants.createPDFFolder(Constants.FolderquestionImages);
            if(imagePath.contains(questionImagePath)){
                Bitmap bmp = BitmapFactory.decodeFile(imagePath);
                image.setImageBitmap(bmp);
            }else {
                Picasso.with(getApplicationContext()).load(imagePath).placeholder(R.drawable.ic_launcher).into(image);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                    finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ShowImage()
    {
        if(isNetworkConnected())
            new LoadImage().execute(ImageUrl);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
//            case R.id.Note:
//                            ShowImage();
//                            break;
        }
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("wait....");
            pDialog.show();
        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {

            if(image != null){
                image.setImageBitmap(bitmap);
                pDialog.dismiss();

            }
            else
            {
            }
        }
    }
}