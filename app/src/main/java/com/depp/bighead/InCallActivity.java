package com.depp.bighead;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.PathInterpolator;
import android.widget.ImageView;

public class InCallActivity extends Activity {

    private View mHeadContainer;
    private ImageView mHeadImage;
    private ImageView mHeadImageExtra;
    private ImageView mHeadImageDest;
    private boolean mImageLoaded = false;

    private final static int MSG_LOAD_HEAD_IMAGE = 1;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_HEAD_IMAGE:
                    doLoadImage();
                    break;
                default:
                    break;

            }
        }
    };

    private HeadBitmapHelper mBitmapHelper;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_incall);

        mBitmapHelper = new HeadBitmapHelper(this);

        mHeadContainer = findViewById(R.id.head_container);
        mHeadImage = (ImageView) findViewById(R.id.head_img);
        mHeadImageExtra = (ImageView) findViewById(R.id.head_img_extra);
        mHeadImageDest = (ImageView) findViewById(R.id.head_img_dest);
        findViewById(R.id.callcard).setVisibility(View.INVISIBLE);

        findViewById(R.id.endButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InCallActivity.this.finish();
            }
        });
    }

    public void onResume() {
        super.onResume();
        if (!mImageLoaded) {
            mImageLoaded = true;
            mHandler.sendEmptyMessageDelayed(MSG_LOAD_HEAD_IMAGE, 500);
        }
    }


    public void onDestroy() {
        super.onDestroy();
        mBitmapHelper = null;
    }

    private void doLoadImage() {
        findViewById(R.id.callcard).setVisibility(View.VISIBLE);
        mBitmapHelper.generateBitmapExtra(new HeadBitmapHelper.LoadCallback() {
            @Override
            public void onExceuteDone() {
                mHeadImage.setImageBitmap(mBitmapHelper.getCurBitmap());
                mHeadImageExtra.setImageBitmap(mBitmapHelper.getBitmapExtra());

                mHeadImageDest.setImageBitmap(mBitmapHelper.getDestBitmap());
                mHeadContainer.setVisibility(View.VISIBLE);
                mHeadContainer.startAnimation(getHaedAnimatior());
            }
        });
    }


    private AnimationSet getHaedAnimatior() {
        AnimationSet animationSet = new AnimationSet(false);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(700);
        alphaAnimation.setInterpolator(new PathInterpolator(0.33f, 0f, 0.67f, 1f));
        animationSet.addAnimation(alphaAnimation);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        return animationSet;
    }
}
