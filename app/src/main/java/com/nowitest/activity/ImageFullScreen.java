package com.nowitest.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.nowitest.R;
import com.nowitest.util.Constants;
import com.squareup.picasso.Picasso;

/**
 * Created by mobintia on 8/9/16.
 */
public class ImageFullScreen extends Activity {

    private ImageView activity_fullsize_imageview, activity_fullsize_imageview_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullsize_imageview);



        activity_fullsize_imageview = (ImageView) findViewById(R.id.activity_fullsize_imageview);
        activity_fullsize_imageview_close = (ImageView) findViewById(R.id.activity_fullsize_imageview_close);

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
                activity_fullsize_imageview.setImageBitmap(bmp);
            }else {
                Picasso.with(getApplicationContext()).load(imagePath).placeholder(R.drawable.ic_launcher).into(activity_fullsize_imageview);
            }
        }

        activity_fullsize_imageview_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}