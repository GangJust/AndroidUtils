package com.freegang.androidutils.color;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;

/**
 * 颜色工具类
 */
public class GColorUtil {

    private GColorUtil() {
        ///
    }

    /**
     * 生成一个随机颜色
     *
     * @return
     */
    @ColorInt
    public static int randomColor() {
        int r = (int) (Math.random() * 255);
        int g = (int) (Math.random() * 255);
        int b = (int) (Math.random() * 255);
        return Color.rgb(r, g, b);
    }

    /**
     * 生成一个随机颜色
     *
     * @param alpha 指定透明度
     * @return
     */
    @ColorInt
    public static int randomColor(int alpha) {
        int r = (int) (Math.random() * 255);
        int g = (int) (Math.random() * 255);
        int b = (int) (Math.random() * 255);
        return Color.argb(alpha, r, g, b);
    }

    /**
     * 生成一个随机颜色
     *
     * @param alpha
     * @return
     */
    @ColorInt
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int randomColor(float alpha) {
        float r = (float) (Math.random() * 255);
        float g = (float) (Math.random() * 255);
        float b = (float) (Math.random() * 255);
        return Color.argb(alpha, r, g, b);
    }
}
