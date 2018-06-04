package com.depp.bighead;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private final static int[] sDrawableId = new int[]{
            R.drawable.head1,
            R.drawable.head2,
            R.drawable.head3,
            R.drawable.head4,
            R.drawable.head5,
            R.drawable.head6,
            R.drawable.head7,
            R.drawable.head8,
            R.drawable.head9,
            R.drawable.head10,
            R.drawable.head11,
    };


    private static int sHeadIndex = 0;
    private ImageView mHeadImageView;
    private ImageView mFrontLayer;

    private HeadBitmapHelper mHelper = new HeadBitmapHelper();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHeadImageView = (ImageView) findViewById(R.id.head_img);
        mFrontLayer = (ImageView) findViewById(R.id.front_layer);

        mHeadImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sHeadIndex++;
                if (sHeadIndex >= sDrawableId.length) {
                    sHeadIndex = 0;
                }

                mHeadImageView.setImageResource(sDrawableId[sHeadIndex]);
                mHeadImageView.setDrawingCacheEnabled(true);

                Bitmap bitmap = mHeadImageView.getDrawingCache();
                mHelper.setBitmap(bitmap);

                mHeadImageView.setDrawingCacheEnabled(false); //释放内存，否则容易异常

            }
        });


    }
}

