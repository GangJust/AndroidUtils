package com.freegang.androidutils.bitmap;

import android.content.Context;
import android.graphics.drawable.*;
import android.graphics.*;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class GBitmapUtils {

    private GBitmapUtils() {
        ///
    }

    /**
     * bitmap -> bytes 推荐使用
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmap2bytes(Bitmap bitmap) {
        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getWidth() * bitmap.getHeight() * 4);
        bitmap.copyPixelsToBuffer(buffer);
        byte[] data = buffer.array();
        buffer.clear();
        return data;
    }

    /**
     * bitmap -> bytes 无压缩有损(如出现图片失真、花图等情况请调用 bitmap2bytes(Bitmap))
     *
     * @param bitmap
     * @param compressFormat
     * @return
     */
    public static byte[] bitmap2bytes(Bitmap bitmap, Bitmap.CompressFormat compressFormat) {
        byte[] bytes = new byte[0];
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            bitmap.compress(compressFormat, 100, out);
            bytes = out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * bytes -> bitmap
     *
     * @param bytes
     * @return
     */
    public static Bitmap bytes2bitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
    }

    /**
     * bytes -> bitmap
     *
     * @param bytes
     * @param opts
     * @return
     */
    public static Bitmap bytes2bitmap(byte[] bytes, BitmapFactory.Options opts) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
    }

    /**
     * bitmap -> drawable
     *
     * @param bitmap
     * @return
     */
    public static Drawable bitmap2drawable(Bitmap bitmap, Context context) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    /**
     * drawable -> bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawable2bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            return bitmapDrawable.getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        //图片位深，PixelFormat.OPAQUE代表没有透明度，RGB_565就是没有透明度的位深，否则就用ARGB_8888。
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 获取drawable资源中的bitmap
     *
     * @param context
     * @param drawableId
     * @return
     */
    public static Bitmap getResourceBitmap(Context context, @DrawableRes int drawableId) {
        return BitmapFactory.decodeResource(context.getResources(), drawableId);
    }

    /**
     * bitmap缩放
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        float scaleWidth = (float) width / w;
        float scaleHeight = (float) height / h;

        //缩放矩阵
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * bitmap圆角
     *
     * @param bitmap
     * @param radius
     * @return
     */
    public static Bitmap setRoundCornerBitmap(Bitmap bitmap, float radius) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 创建输出bitmap对象
        Bitmap outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectf = new RectF(rect);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);

        Canvas canvas = new Canvas(outBitmap);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectf, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return outBitmap;
    }

    /**
     * 将bitmap转换为本地的图片
     *
     * @param bitmap
     * @return
     */
    public static boolean bitmap2Path(Bitmap bitmap, String path, String filename) {
        try {
            OutputStream output = new FileOutputStream(new File(path, filename));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            GLogUtils.xException(e.getMessage());
        }
        return false;
    }
}
