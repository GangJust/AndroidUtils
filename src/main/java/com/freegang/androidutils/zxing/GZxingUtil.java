package com.freegang.androidutils.zxing;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * 二维码生成工具, 需要 zxing 库的支持
 */

public class GZxingUtil {

    private GZxingUtil() {
        ///
    }

    /**
     * 创建二维码
     *
     * @param content     二维码内容
     * @param width       二维码宽度
     * @param height      二维码高度
     * @param blackColor  二维码颜色(黑色部分, 十六进制整数, 例: 0xFF000000)
     * @param whiteColor  二维码颜色(白色部分, 十六进制整数, 例: 0xFFFFFFFF)
     * @param whiteMargin 二维码外白边距离
     * @return bitmap
     */
    public static Bitmap createQrCode(String content, int width, int height, int blackColor, int whiteColor, int whiteMargin) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height);

            matrix = reduceWhite(matrix, 10);
            width = matrix.getWidth(); //重新获取宽度
            height = matrix.getHeight(); //重新获取高度

            // 矩阵转换, 构建 Bitmap
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (matrix.get(j, i)) {
                        pixels[i * width + j] = blackColor;
                    } else {
                        pixels[i * width + j] = whiteColor;
                    }
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param content    二维码内容
     * @param width      二维码宽度
     * @param height     二维码高度
     * @param blackColor 二维码颜色(黑色部分, 十六进制整数, 例: 0xFF000000)
     * @param whiteColor 二维码颜色(白色部分, 十六进制整数, 例: 0xFFFFFFFF)
     * @return
     */
    public static Bitmap createQrCode(String content, int width, int height, int blackColor, int whiteColor) {
        return createQrCode(content, width, height, blackColor, whiteColor, 10);
    }

    /**
     * @param content     二维码内容
     * @param width       二维码宽度
     * @param height      二维码高度
     * @param whiteMargin 二维码外白边距离
     * @return
     */
    public static Bitmap createQrCode(String content, int width, int height, int whiteMargin) {
        return createQrCode(content, width, height, 0xFF000000, 0xFFffffff, whiteMargin);
    }

    /**
     * @param content 二维码内容
     * @param width   二维码宽度
     * @param height  二维码高度
     * @return
     */
    public static Bitmap createQrCode(String content, int width, int height) {
        return createQrCode(content, width, height, 10);
    }

    /**
     * 缩小生成二维码白边框(删除白边 重新添加新白边)
     *
     * @param matrix
     * @return
     */
    private static BitMatrix reduceWhite(BitMatrix matrix, int margin) {
        int tempM = margin * 2;
        int[] rec = matrix.getEnclosingRectangle(); // 获取二维码图案的属性
        int resWidth = rec[2] + tempM;
        int resHeight = rec[3] + tempM;
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight); // 按照自定义边框生成新的BitMatrix
        resMatrix.clear();
        for (int i = margin; i < resWidth - margin; i++) { // 循环，将二维码图案绘制到新的bitMatrix中
            for (int j = margin; j < resHeight - margin; j++) {
                if (matrix.get(i - margin + rec[0], j - margin + rec[1])) {
                    resMatrix.set(i, j);
                }
            }
        }
        return resMatrix;
    }

    /**
     * Drawable To Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);

        return bitmap;
    }
}
