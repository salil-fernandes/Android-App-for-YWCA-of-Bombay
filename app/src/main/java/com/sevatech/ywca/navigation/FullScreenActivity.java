package com.sevatech.ywca.navigation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.sevatech.ywca.R;
import com.sevatech.ywca.navigation.initiatives.InitiativesFullScreenActivity;
import com.squareup.picasso.Picasso;

public class FullScreenActivity extends AppCompatActivity {

    private ScaleGestureDetector mScaleGestureDetector;
    float mScaleFactor = 1.0f;
    ImageView fullScreenImageView;
    ImageButton closeButton;
    String imageUrl;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_gradient));
        }

        int o = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(o);

//        imageUrl = String.valueOf(getIntent().getIntExtra("imageUrl",0));
        imageUrl = getIntent().getStringExtra("imageUrl");

        fullScreenImageView = findViewById(R.id.fullScreen_imageView);
        closeButton = findViewById(R.id.fullScreen_close);

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        fullScreenImageView.setImageResource(imageUrl);
        Picasso.get().load(imageUrl).into(fullScreenImageView);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= mScaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(1f,Math.min(mScaleFactor, 2.0f));
            fullScreenImageView.setScaleX(mScaleFactor);
            fullScreenImageView.setScaleY(mScaleFactor);
            return true;
        }
    }
}
