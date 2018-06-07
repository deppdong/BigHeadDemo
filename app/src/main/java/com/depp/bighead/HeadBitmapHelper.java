package com.depp.bighead;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;

public class HeadBitmapHelper {


    /**
     * 截取图片区域的宽度比例，必须小于等于1
     */
    private final static float WIDTH_PERCENT = 0.5f;
    /**
     * 截取图片区域的高度比例，必须小于等于1
     */
    private final static float HEIGHT_PERCENT = 0.2f;


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
    private Bitmap mMixedFrontLayerBitmap;

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


    public HeadBitmapHelper(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        sScreenWidth = dm.widthPixels;
        sScreenHeight = dm.heightPixels;

        //默认蒙曾
        mDefFrontLayerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bighead_front_layer);
    }


    public void generatePrimaryBitmap(Bitmap bitmap, LoadCallback callback) {
        if (mSrcBitmap != null) {
            mSrcBitmap.recycle();
        }

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


    public Bitmap getMixedFrontLayerBitmap() {
        return mMixedFrontLayerBitmap;
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
            int destWidth = (int) (width * WIDTH_PERCENT);
            int destHeight = (int) (height * HEIGHT_PERCENT);

            // 图片像素Ｘ|Ｙ轴偏移像素数
            int offsetX = (width - destWidth) / 2;
            int offsetY = height - destHeight;

            // 获取制定区域像素
            int[] pixels = new int[destWidth * destHeight];
            mSrcBitmap.getPixels(pixels, 0, destWidth, offsetX, offsetY, destWidth, destHeight);

            getPrimaryColor(pixels);

            //透明化处理
            doAlpha(pixels, destWidth, destHeight);
            mCutBitmap = Bitmap.createBitmap(pixels, 0, destWidth, destWidth, destHeight,
                    Bitmap.Config.ARGB_8888);


            // TODO 生成混合蒙层图
//            generateMixFrontLayerBitmap();
            generateCutFrontLayerBitmap();
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

        mCutColor = Color.argb(countAlpha / PIXEL_SIZE,
                countRed / PIXEL_SIZE, countGreen / PIXEL_SIZE, countBlue / PIXEL_SIZE);


        return mCutColor;
    }

    private int getPrimaryColorByWeight(int[] pixels) {
        int[] colorArray = new int[pixels.length];
        System.arraycopy(pixels, 0, colorArray, 0, pixels.length);

        sortCore(colorArray, 0, colorArray.length);

        return mCutColor;
    }

    /**
     * 生成取色蒙层
     *
     * @return
     */
    private Bitmap generateCutFrontLayerBitmap() {
        int width = sScreenWidth;
        int height = sScreenHeight;

        int destRed = Color.red(mCutColor);
        int destGreen = Color.green(mCutColor);
        int destBlue = Color.blue(mCutColor);

        int[] mixedPixels = new int[width * height];

        int srcHeight = mSrcBitmap.getHeight();
        int alphaHeight = (int) srcHeight / 4;
        int offsetY = srcHeight - alphaHeight;

        for (int j = offsetY; j < height; j++) {
            int destAlpha = 255;
            if (j < srcHeight && j >= offsetY) {
                destAlpha = 255 - 255 * (srcHeight - j) / alphaHeight;
            }
            int destColor = Color.argb(destAlpha, destRed, destGreen, destBlue);

            for (int i = 0; i < width; i++) {
                mixedPixels[j * width + i] = destColor;
            }
        }


        mMixedFrontLayerBitmap = Bitmap.createBitmap(mixedPixels, 0, width, width, height,
                Bitmap.Config.ARGB_8888);

        return mMixedFrontLayerBitmap;
    }


    /**
     * @return
     * @deprecated 合成取色图+视觉提供的默认蒙层图
     */

    private Bitmap generateMixFrontLayerBitmap() {
        int width = sScreenWidth;
        int height = sScreenHeight;

        int cutRed = Color.red(mCutColor);
        int cutGreen = Color.green(mCutColor);
        int cutBlue = Color.blue(mCutColor);

        int[] mixedPixels = new int[width * height];

        int srcHeight = mSrcBitmap.getHeight();
        int alphaHeight = (int) (srcHeight * HEIGHT_PERCENT) * 2;
        int offsetY = srcHeight - alphaHeight;


        // 获取默认蒙层
        int defWidth = mDefFrontLayerBitmap.getWidth();
        int defHeigth = mDefFrontLayerBitmap.getHeight();
        int[] defPixel = new int[defHeigth];

        mDefFrontLayerBitmap.getPixels(defPixel, 0, 1, 0, 0, 1, defHeigth);

        for (int j = 0; j < height; j++) {
            int cutAlpha = 255;
            int destColor = 0;
            if ( j < offsetY) {
                cutAlpha = 0;
            } else    if (j >= offsetY) {
                if (j < srcHeight && j >= offsetY) {
                    cutAlpha = 255 - 255 * (srcHeight - j) / alphaHeight;
                }

                int defAlpha = Color.alpha(defPixel[j]);
                int defRed = Color.red(defPixel[j]);
                int defGreen = Color.green(defPixel[j]);
                int defBlue = Color.blue(defPixel[j]);


                int mixedAlpha = cutAlpha > defAlpha ? defAlpha : cutAlpha;
                int mixedRed = (cutRed + defRed) / 2;
                int mixedGreen = (cutGreen + defGreen) / 2;
                int mixedBlue = (cutBlue + defBlue) / 2;

                destColor = Color.argb(mixedAlpha, mixedRed, mixedGreen, mixedBlue);
            }

            for (int i = 0; i < width; i++) {
                if (j < offsetY) {
//                    mixedPixels[j * width + i] = defPixel[j];
                } else {
                    mixedPixels[j * width + i] = destColor;
                }
            }
        }


        mMixedFrontLayerBitmap = Bitmap.createBitmap(mixedPixels, 0, width, width, height,
                Bitmap.Config.ARGB_8888);

        return mMixedFrontLayerBitmap;
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

    private void sort(int[] colors){


//         Collections.sort(intList,new Comparator<Integer>() {
//
//            @Override
//            public int compare(Integer o1, Integer o2) {
//                // 返回值为int类型，大于0表示正序，小于0表示逆序
//                return o2-o1;
//            }
//        });
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


    private void log(String msg) {
        Log.d("HeadBitmapHelper", msg);
    }
}
