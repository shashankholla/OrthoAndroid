package com.sharcodes.ortho;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.indicator.progresspie.ProgressPieIndicator;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.github.piasy.biv.view.BigImageView;

public class ImageViwer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BigImageViewer.initialize(GlideImageLoader.with(getApplicationContext()));
        setContentView(R.layout.activity_image_viwer);


        BigImageView bigImageView = (BigImageView) findViewById(R.id.mBigImage);
        bigImageView.setProgressIndicator(new ProgressPieIndicator());
        bigImageView.showImage(Uri.parse(getIntent().getStringExtra("image")));
    }
}