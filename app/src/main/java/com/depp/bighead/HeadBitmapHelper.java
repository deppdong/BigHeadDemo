package com.depp.bighead;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HeadBitmapHelper {


    /**
     * 截取图片区域的宽度比例，必须小于等于1
     */
    private final static float CUT_WIDTH_PERCENT = 0.5f;
    /**
     * 截取图片区域的高度比例，必须小于等于1
     */
    private final static float CUT_HEIGHT_PERCENT = 0.1f;


    /**
     * 原图
     */
    private Bitmap mSrcBitmap;

    /**
     * 裁剪区域图
     */
    private Bitmap mCutBitmap;
    /**
     * 裁剪区域取色值
     */
    private int mCutColor;

    /**
     * 默认蒙层
     */
    private Bitmap mDefFrontLayerBitmap;
    /**
     * 根据取色生成的渐变混合蒙层图
     */
    private Bitmap mBitmapExtra;

    /**
     * 屏幕宽
     */
    private static int sScreenWidth;
    /**
     * 屏幕高
     */
    private static int sScreenHeight;


    public static interface LoadCallback {
        void onExceuteDone();
    }

    private Context mContext;


    public HeadBitmapHelper(Context context) {
        mContext = context.getApplicationContext();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        sScreenWidth = dm.widthPixels;
        sScreenHeight = dm.heightPixels;

        //默认蒙曾
        mDefFrontLayerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bighead_front_layer);
    }

    public void generateBitmapExtra(LoadCallback callback) {
        if (mSrcBitmap != null) {
            mSrcBitmap.recycle();
        }

        int resId = HeadImages.sDrawableId[HeadImages.sIndex];
        mSrcBitmap = BitmapFactory.decodeResource(mContext.getResources(), resId);

        generatePrimaryBitmap(mSrcBitmap, callback);
    }

    private void generatePrimaryBitmap(Bitmap bitmap, LoadCallback callback) {

        if (bitmap == null) {
            mSrcBitmap = null;
            return;
        }

        mSrcBitmap = bitmap;

        new BitmapTask(callback).execute();
    }

    public Bitmap getCutBitmap() {
        return mCutBitmap;
    }

    public Bitmap getCurBitmap(){
        return mSrcBitmap;
    }

    public Bitmap getBitmapExtra() {
        return mBitmapExtra;
    }

    private class BitmapTask extends AsyncTask<Void, Void, Void> {

        LoadCallback callback;

        public BitmapTask(LoadCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int width = mSrcBitmap.getWidth();
            int height = mSrcBitmap.getHeight();

            log("srcBitmap w/h: " + width + "/" + height);

            // 取色区域(底部)：（width:0-width; height: offsetHeight-height)
            int destWidth = (int) (width * CUT_WIDTH_PERCENT);
            int destHeight = (int) (height * CUT_HEIGHT_PERCENT);

            // 图片像素Ｘ|Ｙ轴偏移像素数
            int offsetX = (width - destWidth) / 2;
            int offsetY = height - destHeight;

            // 获取制定区域像素
            int[] pixels = new int[destWidth * destHeight];
            mSrcBitmap.getPixels(pixels, 0, destWidth, offsetX, offsetY, destWidth, destHeight);

//            getPrimaryColor(pixels);
            getPrimaryColorByWeight(pixels);

            //透明化处理
            doAlpha(pixels, destWidth, destHeight);
            mCutBitmap = Bitmap.createBitmap(pixels, 0, destWidth, destWidth, destHeight, Bitmap.Config.ARGB_8888);


            // TODO 生成混合蒙层图
//            generateMixFrontLayerBitmap();
            generateBitmapExtra();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (callback != null) {
                callback.onExceuteDone();
            }
        }
    }

    private void doAlpha(int[] pixels, int width, int height) {
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int desAlpha = 255 * (height - j) / height;
                int color = pixels[j * width + i];

                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);

                pixels[j * width + i] = Color.argb(desAlpha, red, green, blue);
            }
        }

    }

    /**
     * 获取图片正下方中间位置颜色平均值
     *
     * @return
     */
    private int getPrimaryColor(int[] pixels) {
        int countAlpha = 0;
        int countRed = 0;
        int countGreen = 0;
        int countBlue = 0;

        final int PIXEL_SIZE = pixels.length;
        for (int k = 0; k < pixels.length; k++) {
            countAlpha += Color.alpha(pixels[k]);
            countRed += Color.red(pixels[k]);
            countGreen += Color.green(pixels[k]);
            countBlue += Color.blue(pixels[k]);
        }
        log("argb(TOTAL): " + countAlpha + ", " + countRed + ", " + countGreen + ", " + countBlue + " SIZE:" + PIXEL_SIZE);

        mCutColor = Color.argb(countAlpha / PIXEL_SIZE, countRed / PIXEL_SIZE, countGreen / PIXEL_SIZE, countBlue / PIXEL_SIZE);


        return mCutColor;
    }

    private int getPrimaryColorByWeight(int[] pixels) {
        int[] result = sortResult(pixels);

        return getPrimaryColor(result);
    }

    /**
     * 生成取色蒙层
     *
     * @return
     */
    private Bitmap generateBitmapExtra() {
        int width = sScreenWidth;
        int height = sScreenHeight;

        int destRed = Color.red(mCutColor);
        int destGreen = Color.green(mCutColor);
        int destBlue = Color.blue(mCutColor);

        int[] mixedPixels = new int[width * height];

        int srcHeight = sScreenWidth;
        int alphaHeight = (int) sScreenWidth / 3;
        int offsetY = srcHeight - alphaHeight;

        for (int j = offsetY+1; j < height; j++) {
            int destAlpha = 255;
            if (j < srcHeight && j > offsetY) {
                destAlpha = 255 - 255 * (srcHeight - j) / alphaHeight;
            }
            int destColor = Color.argb(destAlpha, destRed, destGreen, destBlue);

            for (int i = 0; i < width; i++) {
                mixedPixels[j * width + i] = destColor;
            }
        }


        mBitmapExtra = Bitmap.createBitmap(mixedPixels, 0, width, width, height, Bitmap.Config.ARGB_8888);

        return mBitmapExtra;
    }

    /*
     * 排序的核心算法
     *
     * @param array
     *      待排序数组
     * @param startIndex
     *      开始位置
     * @param endIndex
     *      结束位置
     */
    private void sortCore(int[] array, int startIndex, int endIndex) {
        if (startIndex >= endIndex) {
            return;
        }

        int boundary = boundary(array, startIndex, endIndex);

        sortCore(array, startIndex, boundary - 1);
        sortCore(array, boundary + 1, endIndex);
    }

    /*
     * 交换并返回分界点
     *
     * @param array
     *      待排序数组
     * @param startIndex
     *      开始位置
     * @param endIndex
     *      结束位置
     * @return
     *      分界点
     */
    private int boundary(int[] array, int startIndex, int endIndex) {
        int standard = array[startIndex]; // 定义标准
        int leftIndex = startIndex; // 左指针
        int rightIndex = endIndex; // 右指针

        while (leftIndex < rightIndex) {
            while (leftIndex < rightIndex && array[rightIndex] >= standard) {
                rightIndex--;
            }
            array[leftIndex] = array[rightIndex];

            while (leftIndex < rightIndex && array[leftIndex] <= standard) {
                leftIndex++;
            }
            array[rightIndex] = array[leftIndex];
        }

        array[leftIndex] = standard;
        return leftIndex;
    }

    private int[] sortResult(int[] pixels) {

        List<WeightColor> list = new ArrayList<WeightColor>();

        for (int i = 0; i < pixels.length; i++) {
            list.add(new WeightColor(pixels[i]));
        }

        Collections.sort(list, new Comparator<WeightColor>() {
            @Override
            public int compare(WeightColor wc1, WeightColor wc2) {
                if (wc1.weight > wc2.weight) {
                    return 1;
                } else if(wc1.weight == wc2.weight) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });


        int[] result = new int[list.size()*3/5];
        for (int i = 0; i < result.length; i++) {
            result[i] = list.get(i).color;
        }

        return result;
    }


    private static class WeightColor {
        int color;
        float weight;

        private WeightColor(int color) {
            this.color = color;
            this.weight = getColorWeight(color);
        }

        /**
         * 获取颜色深度权重，权重越大，颜色越浅
         */
        private float getColorWeight(int color) {
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            return r * 0.299f + g * 0.578f + b * 0.114f;
        }

        public String toString(){
            return "color:"+color+", weight:"+weight;
        }
    }


    private void log(String msg) {
        Log.d("HeadBitmapHelper", msg);
    }
}
