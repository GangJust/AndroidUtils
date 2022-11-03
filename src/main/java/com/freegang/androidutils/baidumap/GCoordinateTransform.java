package com.freegang.androidutils.baidumap;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by Daniel on 2016/7/27.
 * <p>
 * 提供了百度坐标（BD09）、国测局坐标（火星坐标，GCJ02）、和WGS84坐标系之间的转换
 * <p>
 * 会有偏差，但是偏差在可接受范围之内
 * <p>
 * 参考: https://www.jianshu.com/p/47572eb39156
 */
public class GCoordinateTransform {
    static double x_PI = 3.14159265358979324 * 3000.0 / 180.0;
    static double PI = 3.1415926535897932384626;
    static double a = 6378245.0;
    static double ee = 0.00669342162296594323;
    static double[] MCBAND = new double[]{12890594.86, 8362377.87, 5591021, 3481989.83, 1678043.12, 0};
    static double[][] MC2LL = new double[][]{
            {1.410526172116255e-8, 0.00000898305509648872, -1.9939833816331, 200.9824383106796, -187.2403703815547, 91.6087516669843, -23.38765649603339, 2.57121317296198, -0.03801003308653, 17337981.2},
            {-7.435856389565537e-9, 0.000008983055097726239, -0.78625201886289, 96.32687599759846, -1.85204757529826, -59.36935905485877, 47.40033549296737, -16.50741931063887, 2.28786674699375, 10260144.86},
            {-3.030883460898826e-8, 0.00000898305509983578, 0.30071316287616, 59.74293618442277, 7.357984074871, -25.38371002664745, 13.45380521110908, -3.29883767235584, 0.32710905363475, 6856817.37},
            {-1.981981304930552e-8, 0.000008983055099779535, 0.03278182852591, 40.31678527705744, 0.65659298677277, -4.44255534477492, 0.85341911805263, 0.12923347998204, -0.04625736007561, 4482777.06},
            {3.09191371068437e-9, 0.000008983055096812155, 0.00006995724062, 23.10934304144901, -0.00023663490511, -0.6321817810242, -0.00663494467273, 0.03430082397953, -0.00466043876332, 2555164.4},
            {2.890871144776878e-9, 0.000008983055095805407, -3.068298e-8, 7.47137025468032, -0.00000353937994, -0.02145144861037, -0.00001234426596, 0.00010322952773, -0.00000323890364, 826088.5}
    };

    /**
     * 百度坐标系 (BD-09) 与 火星坐标系 (GCJ-02)的转换
     * 即 百度 转 谷歌、高德
     *
     * @param bd_lon
     * @param bd_lat
     * @returns {[]} GCJ-02 坐标：[经度，纬度]
     */
    public static LatLng transformBD09ToGCJ02(double bd_lon, double bd_lat) {
        double x = bd_lon - 0.0065;
        double y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_PI);
        double gg_lng = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new LatLng(gg_lat, gg_lng);
    }

    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换
     * 即谷歌、高德 转 百度
     *
     * @param lng
     * @param lat
     * @returns {[]}  BD-09 坐标：[经度，纬度]
     */
    public static LatLng transformGCJ02ToBD09(double lng, double lat) {
        double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * x_PI);
        double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * x_PI);
        double bd_lng = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new LatLng(bd_lat, bd_lng);
    }

    /**
     * WGS84转GCj02
     *
     * @param lng
     * @param lat
     * @returns {[]} GCj02 坐标：[经度，纬度]
     */
    public static LatLng transformWGS84ToGCJ02(double lng, double lat) {
        if (outOfChina(lng, lat)) {
            return new LatLng(lat, lng);
        } else {
            double dLat = transformLat(lng - 105.0, lat - 35.0);
            double dLng = transformLng(lng - 105.0, lat - 35.0);
            double radLat = lat / 180.0 * PI;
            double magic = Math.sin(radLat);
            magic = 1 - ee * magic * magic;
            double sqrtMagic = Math.sqrt(magic);
            dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI);
            dLng = (dLng * 180.0) / (a / sqrtMagic * Math.cos(radLat) * PI);
            double mgLat = lat + dLat;
            double mgLng = lng + dLng;
            return new LatLng(mgLat, mgLng);
        }
    }

    /**
     * GCJ02 转换为 WGS84
     *
     * @param lng
     * @param lat
     * @returns {[]} WGS84 坐标：[经度，纬度]
     */
    public static LatLng transformGCJ02ToWGS84(double lng, double lat) {
        if (outOfChina(lng, lat)) {
            return new LatLng(lat, lng);
        } else {
            double dLat = transformLat(lng - 105.0, lat - 35.0);
            double dLng = transformLng(lng - 105.0, lat - 35.0);
            double radLat = lat / 180.0 * PI;
            double magic = Math.sin(radLat);
            magic = 1 - ee * magic * magic;
            double sqrtMagic = Math.sqrt(magic);
            dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI);
            dLng = (dLng * 180.0) / (a / sqrtMagic * Math.cos(radLat) * PI);
            double mgLat = lat + dLat;
            double mgLng = lng + dLng;
            return new LatLng(lat * 2 - mgLat, lng * 2 - mgLng);
        }
    }

    public static LatLng transformLonLatToMecator(double lng, double lat) {
        double earthRad = 6378137.0;
        double x = lng * Math.PI / 180 * earthRad;
        double a = lat * Math.PI / 180;
        double y = earthRad / 2 * Math.log((1.0 + Math.sin(a)) / (1.0 - Math.sin(a)));
        return new LatLng(y, x); //[12727039.383734727, 3579066.6894065146]
    }

    /**
     * 百度坐标BD09 转 WGS84
     *
     * @param lng 经度
     * @param lat 纬度
     * @return {[]} WGS84 坐标：[经度，纬度]
     */
    public static LatLng transformBD09ToWGS84(double lng, double lat) {
        LatLng latLng = transformBD09ToGCJ02(lng, lat);
        return transformGCJ02ToWGS84(latLng.latitude, latLng.longitude);
    }

    /**
     * WGS84 转 百度坐标BD09
     *
     * @param lng 经度
     * @param lat 纬度
     * @return {[]} BD09 坐标：[经度，纬度]
     */
    public static LatLng transformWGS84ToBD09(double lng, double lat) {
        LatLng latLng = transformWGS84ToGCJ02(lng, lat);
        return transformGCJ02ToBD09(latLng.latitude, latLng.longitude);
    }

    private static double transformLat(double lng, double lat) {
        double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLng(double lng, double lat) {
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * 判断是否在国内，不在国内则不做偏移
     *
     * @param lng
     * @param lat
     * @returns {boolean}
     */
    public static boolean outOfChina(double lng, double lat) {
        return (lng < 72.004 || lng > 137.8347) || (lat < 0.8293 || lat > 55.8271);
    }

    /**
     * 墨卡托坐标转WGS
     *
     * @param lng
     * @param lat
     * @return
     */
    public static LatLng BD_MKT2WGS(double lng, double lat) {
        double[] cF = null;
        lng = Math.abs(lng);
        lat = Math.abs(lat);
        for (int cE = 0; cE < MCBAND.length; cE++) {
            if (lat >= MCBAND[cE]) {
                cF = MC2LL[cE];
                break;
            }
        }
        if (cF == null) {
            return new LatLng(0, 0);
        }

        lng = cF[0] + cF[1] * Math.abs(lng);
        double cC = Math.abs(lat) / cF[9];
        lat = cF[2] + cF[3] * cC + cF[4] * cC * cC + cF[5] * cC * cC * cC + cF[6] * cC * cC * cC * cC + cF[7] * cC * cC * cC * cC * cC + cF[8] * cC * cC * cC * cC * cC * cC;
        lng *= (lng < 0 ? -1 : 1);
        lat *= (lat < 0 ? -1 : 1);
        return new LatLng(lat, lng);
    }
}
