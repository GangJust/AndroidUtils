package com.freegang.androidutils.assets;

import android.app.Application;
import android.content.res.AssetManager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GAssetsUtils {

    private GAssetsUtils() {
        ///
    }

    // 获取资产文件内容
    public static String getAssetsContent(Application application, String filename) {
        StringBuilder stringBuilder = new StringBuilder();
        AssetManager assetManager = application.getAssets();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open(filename)))) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    // 获取资产文件数据流
    public static byte[] getAssetsBytes(Application application, String filename) {
        AssetManager assetManager = application.getAssets();
        try (BufferedInputStream stream = new BufferedInputStream(assetManager.open(filename))) {
            List<Byte> byteList = new ArrayList<>();
            int read;
            while ((read = stream.read()) != -1) {
                byteList.add((byte) read);
            }

            byte[] bytes = new byte[byteList.size()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = byteList.get(i);
            }
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    //获取资产文件绝对路径
    public static String getAssetsAbsolutePath(String filename) {
        return "file:///android_asset/" + filename;
    }
}
