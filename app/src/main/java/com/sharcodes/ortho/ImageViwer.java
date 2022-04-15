package com.sharcodes.ortho;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

public class ImageViwer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        BigImageViewer.initialize(GlideImageLoader.with(getApplicationContext()));
        setContentView(R.layout.activity_image_viwer);

        Uri uri = Uri.parse(getIntent().getStringExtra("image"));
        new StfalconImageViewer.Builder<Uri>(this, new Uri[]{uri}, new ImageLoader<Uri>() {
            @Override
            public void loadImage(ImageView imageView, Uri image) {
                Glide.with(ImageViwer.this)
                        .load(image)
                        .into(imageView);
            }

        })
                .withHiddenStatusBar(true)
                .show();


//        BigImageView bigImageView = (BigImageView) findViewById(R.id.mBigImage);
//        bigImageView.setProgressIndicator(new ProgressPieIndicator());
//        bigImageView.showImage(Uri.parse(getIntent().getStringExtra("image")));
    }
}