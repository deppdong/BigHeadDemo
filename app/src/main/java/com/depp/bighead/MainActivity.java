package com.depp.bighead;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements HeadBitmapHelper.LoadCallback {

    private final static int[] sDrawableId = new int[]{
            R.drawable.head1,
            R.drawable.head2,
//            R.drawable.head3,
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

    private ImageView mCutBitmapView;

    private HeadBitmapHelper mHelper;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mHelper = new HeadBitmapHelper(this);

        mHeadImageView = (ImageView) findViewById(R.id.head_img);
        mFrontLayer = (ImageView) findViewById(R.id.front_layer);
        mCutBitmapView = (ImageView) findViewById(R.id.head_img_cache);

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
                mHelper.generatePrimaryBitmap(bitmap, MainActivity.this);


            }
        });


    }


    @Override
    public void onExceuteDone() {
//        mCutBitmapView.setImageBitmap(mHelper.getCutBitmap());
        mHeadImageView.setDrawingCacheEnabled(false); //释放内存，否则容易异常

        mFrontLayer.setImageBitmap(mHelper.getMixedFrontLayerBitmap());
    }
}

