package com.sevatech.ywca.navigation.initiatives;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sevatech.ywca.R;
import com.squareup.picasso.Picasso;

public class InitiativesFullScreenActivity extends AppCompatActivity {

    private ScaleGestureDetector mScaleGestureDetector;
    float mScaleFactor = 1.0f;
    ImageView mImageView;
//    int imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiatives_full_screen);
        mImageView = findViewById(R.id.full_image_view);
        TextView textView = findViewById(R.id.full_text_view);
        ImageButton closeButton = findViewById(R.id.full_close);

        int imageId = getIntent().getIntExtra("image", 0);
        String text = getIntent().getStringExtra("text");
        textView.setText(text);

        mImageView.setImageResource(imageId);
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
            mImageView.setScaleX(mScaleFactor);
            mImageView.setScaleY(mScaleFactor);
            return true;
        }
    }
}
