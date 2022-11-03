package com.freegang.androidutils.baidumap;

import android.graphics.Point;

import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;

/**
 * 百度地图, WMS瓦片图工具类
 * <p>
 * 参考: https://www.jianshu.com/p/47572eb39156
 */
public class GBaiduTileUtils {
    //显示等级数组, 数组长度给定30, 避免Index越界 (百度地图 getMinDisLevel=3, getMaxDisLevel=20)
    public static final double[] disLevel = disLevel();

    private static double[] disLevel() {
        double[] zooms = new double[30];
        for (int i = 0; i < zooms.length; i++) {
            zooms[i] = Math.pow(2, 18 - i);
        }
        return zooms;
    }

    /**
     * 通过瓦片图 x,y,z 获取Wms瓦片BBox
     *
     * @param tileSize 瓦片图大小
     * @param x        瓦片图的x
     * @param y        瓦片图的y
     * @param z        瓦片图的z
     * @return length == 4 的 BBox 数组
     */
    public static double[] getWmsBBox(int tileSize, final int x, final int y, final int z) {
        double res = disLevel[z];
        double minx = x * tileSize * res;
        double miny = y * tileSize * res;
        double maxx = (x + 1) * tileSize * res;
        double maxy = (y + 1) * tileSize * res;

        // 百度墨卡托坐标 -> 百度经纬度坐标
        LatLng bottomLeft = GCoordinateTransform.BD_MKT2WGS(minx, miny);
        LatLng topRight = GCoordinateTransform.BD_MKT2WGS(maxx, maxy);

        //地图旋转可能导致 坐下右上 互换.
        double[] bBox = {bottomLeft.longitude, bottomLeft.latitude, topRight.longitude, topRight.latitude};
        if (bottomLeft.latitude > topRight.latitude) {
            double tmp = bBox[1];
            bBox[1] = bBox[3];
            bBox[3] = tmp;
        }
        if (bottomLeft.longitude > topRight.longitude) {
            double tmp = bBox[0];
            bBox[0] = bBox[2];
            bBox[2] = tmp;
        }
        return bBox;
    }


    /**
     * 通过点击的经纬度与屏幕点位获取瓦片图层的大概BBox(存在小问题)
     *
     * @param level      地图的缩放比例,可通过: MapView.getMapLevel() 获取, 这里的缩放比
     * @param projection 地图屏幕相对位置工具类,可通过 BaiduMap.getProjection() 获取
     * @param layerSize  瓦片图层大小, 按照百度地图的逻辑应该是 256*256 的图层, 网络上又有人说是 64*64 的图层
     * @param latLng     地图上被点击的经纬度, 需要监听地图单击事件, 也可以是你确定的一个点位
     * @return length == 4 的 BBox 数组
     */
    public static double[] getWmsBBox(int level, Projection projection, int layerSize, LatLng latLng) {
        //获取缩放值(瓦片大小)
        int scale = layerSize / level;

        //通过地图经纬度获取相对位置的屏幕坐标
        Point screenLocation = projection.toScreenLocation(latLng);

        //左下角; x左移动, y下移动
        Point bottomLeftPoint = new Point(screenLocation.x - scale, screenLocation.y + scale);
        //右上角; x右移动, y上移动
        Point topRightPoint = new Point(screenLocation.x + scale, screenLocation.y - scale);

        //将屏幕的相对坐标转换为经纬度
        LatLng bottomLeft = projection.fromScreenLocation(bottomLeftPoint);
        LatLng topRight = projection.fromScreenLocation(topRightPoint);

        double[] bBox = {bottomLeft.longitude, bottomLeft.latitude, topRight.longitude, topRight.latitude};

        //地图旋转可能导致 坐下右上 互换.
        if (bottomLeft.latitude > topRight.latitude) {
            double tmp = bBox[1];
            bBox[1] = bBox[3];
            bBox[3] = tmp;
        }
        if (bottomLeft.longitude > topRight.longitude) {
            double tmp = bBox[0];
            bBox[0] = bBox[2];
            bBox[2] = tmp;
        }

        return bBox;
    }
}
