package com.depp.bighead;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.depp.machine.state.StateMachineTest;

public class MainActivity extends AppCompatActivity {


    private ImageView mHeadImageView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
//        NavigationBarUtils.setDarkIconColor(getWindow(), true, true);   //第二个参数设置导航栏图标颜色 true=黑色  false 白色

        mHeadImageView = (ImageView) findViewById(R.id.head_img);
        mHeadImageView.setImageResource(HeadImages.sDrawableId[HeadImages.sIndex]);

        mHeadImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                HeadImages.sIndex++;
                if (HeadImages.sIndex >= HeadImages.sDrawableId.length) {
                    HeadImages.sIndex = 0;
                }

                mHeadImageView.setImageResource(HeadImages.sDrawableId[HeadImages.sIndex]);
                mHeadImageView.setDrawingCacheEnabled(true);

                Bitmap bitmap = mHeadImageView.getDrawingCache();

            }
        });

        StateMachineTest.doTest(this);


        Button dial = (Button) findViewById(R.id.dial);
        dial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, InCallActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.putExtra("img_idx", HeadImages.sIndex);
                MainActivity.this.startActivity(intent);
            }
        });

    }

}

