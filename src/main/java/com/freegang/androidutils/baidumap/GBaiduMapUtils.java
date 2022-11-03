package com.freegang.androidutils.baidumap;

import android.graphics.Color;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.Polygon;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.SpatialRelationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 百度地图工具类
 * 需要接入百度地图SDK
 */
public class GBaiduMapUtils {
    private GBaiduMapUtils() {
        ///
    }

    /// Polygon(含边框)
    private static List<Polygon> polygonList;
    //Polygon边框
    private static Polygon polygonStroke;

    /**
     * 添加多边形悬浮
     *
     * @param baiduMap
     * @param options
     */
    public static void addPolygon(BaiduMap baiduMap, List<PolygonOptions> options) {
        polygonList = new ArrayList<>();
        for (PolygonOptions option : options) {
            Overlay overlay = baiduMap.addOverlay(option);
            polygonList.add((Polygon) overlay);
        }
    }

    /**
     * 增加一个多边形悬浮
     *
     * @param baiduMap
     * @param option
     */
    public static void appendPolygon(BaiduMap baiduMap, PolygonOptions option) {
        if (polygonList != null) return;
        Overlay overlay = baiduMap.addOverlay(option);
        polygonList.add((Polygon) overlay);
    }

    /**
     * 多边形悬浮添加边框
     *
     * @param baiduMap
     * @param parentPolygon 被添加边框的多边形
     * @param stroke        边框
     */
    public static void addPolygonStroke(BaiduMap baiduMap, Polygon parentPolygon, Stroke stroke) {
        PolygonOptions option = new PolygonOptions()
                .points(parentPolygon.getPoints())
                .stroke(stroke)
                .fillColor(Color.TRANSPARENT); //填充色透明
        Overlay overlay = baiduMap.addOverlay(option);
        polygonList.add((Polygon) overlay);
    }

    /**
     * 多边形悬浮添加边框, 同一时间内, 只允许有一个边框出现
     *
     * @param baiduMap
     * @param parentPolygon 被添加边框的多边形
     * @param stroke        边框
     */
    public static void addSinglePolygonStroke(BaiduMap baiduMap, Polygon parentPolygon, Stroke stroke) {
        if (polygonStroke != null && polygonList.contains(polygonStroke)) {
            removeOverlay(baiduMap, polygonStroke);
            polygonList.remove(polygonStroke);
        }
        if (parentPolygon == null) return;
        PolygonOptions option = new PolygonOptions()
                .points(parentPolygon.getPoints())
                .stroke(stroke)
                .fillColor(Color.TRANSPARENT); //填充色透明
        polygonStroke = (Polygon) baiduMap.addOverlay(option);
        polygonList.add(polygonStroke);
    }

    /**
     * 获取所有多边形覆盖物
     *
     * @return
     */
    public static List<Polygon> getPolygonList() {
        return polygonList == null ? new ArrayList<>() : polygonList;
    }

    /**
     * 获取到当前边框
     *
     * @return
     */
    public static Polygon getPolygonStroke() {
        return polygonStroke;
    }

    /**
     * 获取某个点位附近的多边形覆盖物, 如果该点位存在某个多边形覆盖物上, 则直接返回该多边形覆盖物, 否则返回 null
     *
     * @return
     */
    public static Polygon getPolygonByPoint(LatLng point) {
        Polygon result = null;
        for (Overlay overlay : getPolygonList()) {
            Polygon polygon = (Polygon) overlay;
            //判断点击的point是否在某个多边形内部(即多边形被点击)
            boolean isPoint = SpatialRelationUtil.isPolygonContainsPoint(polygon.getPoints(), point);
            if (isPoint) result = polygon;
            //if (isPoint) return polygon;  //如果不管内部如何(嵌套多边形), 那么只需要匹配到返回即可
        }
        return result;
    }

    /// Polyline
    private static List<Polyline> polylineList;

    /**
     * 添加折线悬浮
     *
     * @param baiduMap
     * @param options
     */
    private static void addPolyline(BaiduMap baiduMap, List<PolylineOptions> options) {
        polylineList = new ArrayList<>();
        for (PolylineOptions option : options) {
            appendPolyline(baiduMap, option);
        }
    }

    /**
     * 增加一个折线悬浮
     *
     * @param baiduMap
     * @param option
     */
    public static void appendPolyline(BaiduMap baiduMap, PolylineOptions option) {
        if (polylineList != null) return;
        Overlay overlay = baiduMap.addOverlay(option);
        polylineList.add((Polyline) overlay);
    }

    /**
     * 获取所有折线悬浮
     *
     * @return
     */
    public static List<Polyline> getPolylineList() {
        return polylineList == null ? new ArrayList<>() : polylineList;
    }

    /**
     * 获取某个点位附近的折线覆盖物, 如果该点位存在某个折线覆盖物上, 则直接返回该折线覆盖物, 否则返回 null
     *
     * @return
     */
    public static Polyline getPolylineByPoint(LatLng point) {
        Polyline result = null;
        for (Overlay overlay : getPolylineList()) {
            Polyline polyline = (Polyline) overlay;
            //判断点击的point是否在某个多边形内部(即多边形被点击)
            boolean isPoint = SpatialRelationUtil.isPolygonContainsPoint(polyline.getPoints(), point);
            if (isPoint) result = polyline;
            //if (isPoint) return polyline;  //如果不管内部如何(嵌套多边形), 那么只需要匹配到返回即可
        }
        return result;
    }

    /// Marker
    private static List<Marker> markerList;

    /**
     * 添加标记悬浮
     *
     * @param baiduMap
     * @param options
     */
    public static void addMarker(BaiduMap baiduMap, List<MarkerOptions> options) {
        markerList = new ArrayList<>();
        for (MarkerOptions option : options) {
            Overlay overlay = baiduMap.addOverlay(option);
            markerList.add((Marker) overlay);
        }
    }

    /**
     * 增加一个标记悬浮
     *
     * @param baiduMap
     * @param option
     */
    public static void appendMarker(BaiduMap baiduMap, MarkerOptions option) {
        if (markerList == null) return;
        Overlay overlay = baiduMap.addOverlay(option);
        markerList.add((Marker) overlay);
    }

    /**
     * 获取所有标记悬浮
     *
     * @return
     */
    public static List<Marker> getMarkerList() {
        return markerList == null ? new ArrayList<>() : markerList;
    }

    /// Overlay
    /**
     * 移除某个悬浮
     *
     * @param baiduMap
     * @param overlay
     * @param <O>
     */
    public static <O extends Overlay> void removeOverlay(BaiduMap baiduMap, O overlay) {
        baiduMap.removeOverLays(Collections.singletonList(overlay));

        if (overlay instanceof Polygon) { //移除多边形
            polygonList.remove(overlay);
        } else if (overlay instanceof Polyline) { //移除折线
            polylineList.remove(overlay);
        } else if (overlay instanceof Marker) { //移除标记
            markerList.remove(overlay);
        }
    }

    /**
     * 清空所有悬浮
     *
     * @param baiduMap
     */
    public static void clearAllOverlay(BaiduMap baiduMap) {
        baiduMap.clear();

        if (polygonList != null) polygonList.clear();
        if (polylineList != null) polylineList.clear();
        if (markerList != null) markerList.clear();

        polygonList = null;
        polygonStroke = null;
        polylineList = null;
        markerList = null;
    }
}
