package com.sharcodes.ortho;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.indicator.progresspie.ProgressPieIndicator;
//import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.github.piasy.biv.view.BigImageView;
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