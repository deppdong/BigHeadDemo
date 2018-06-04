package com.depp.bighead;

import android.graphics.Bitmap;
import android.util.Log;

public class HeadBitmapHelper {

    private Bitmap mBitmap;
    private float mPercentHeight = 0.2f;
    private int mPrimaryColorForBigHead;

    public void setBitmap(Bitmap bitmap) {
        if (mBitmap != null) {
            mBitmap.recycle();
        }

        mBitmap = bitmap;

        if (mBitmap != null) {
            mPrimaryColorForBigHead = getPrimaryColorForBigHead();
        } else {
            log("null, clearing cache");
        }

    }

    public int getPrimaryColorForBigHead() {
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        // 取色区域(底部)：（width:0-width; height: offsetHeight-height)
        int offsetHeight = (int) (height * mPercentHeight);

        log("setBitmap  w/h = " + width + "/" + height+" offsetHeight="+offsetHeight);

        int[] pixels = new int[width * (height-offsetHeight)];


        return 0;
    }


    private void log(String msg) {
        Log.d("HeadBitmapHelper", msg);
    }
}
