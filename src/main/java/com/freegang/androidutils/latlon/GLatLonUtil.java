package com.freegang.androidutils.latlon;

/**
 * 经纬度工具类
 */
public class GLatLonUtil {
    private static final double EARTH_RADIUS = 6371000; //地球平均半径, 单位：m

    private static final double EQUATOR_RADIUS = 6378137; //地球赤道半径, 单位：m

    private GLatLonUtil() {
        ///
    }

    /**
     * 获取两个经纬度之间的距离
     *
     * @param lon1 经度1
     * @param lat1 纬度1
     * @param lon2 经度2
     * @param lat2 纬度2
     * @return 返回结果: 单位 m
     */
    public static double getDistance(Double lon1, Double lat1, Double lon2, Double lat2) {
        // 经纬度（角度）转弧度。弧度用作参数，以调用Math.cos和Math.sin
        double radiansAX = Math.toRadians(lon1); // A经弧度
        double radiansAY = Math.toRadians(lat1); // A纬弧度
        double radiansBX = Math.toRadians(lon2); // B经弧度
        double radiansBY = Math.toRadians(lat2); // B纬弧度

        // 公式中“cosβ1cosβ2cos（α1-α2）+sinβ1sinβ2”的部分，得到∠AOB的cos值
        double cos = Math.cos(radiansAY) * Math.cos(radiansBY) * Math.cos(radiansAX - radiansBX) + Math.sin(radiansAY) * Math.sin(radiansBY);
        //System.out.println("cos = " + cos); // 值域[-1,1]
        double acos = Math.acos(cos); // 反余弦值
        //System.out.println("acos = " + acos); // 值域[0,π]
        //System.out.println("∠AOB = " + Math.toDegrees(acos)); // 球心角 值域[0,180]
        return EARTH_RADIUS * acos; // 最终结果
    }

    /**
     * 获取两个经纬度之间的距离
     *
     * @param lon1 经度1
     * @param lat1 纬度1
     * @param lon2 经度2
     * @param lat2 纬度2
     * @return 返回结果: 单位 m
     */
    public static double getDistance(String lon1, String lat1, String lon2, String lat2) {
        return getDistance(
                Double.parseDouble(lon1),
                Double.parseDouble(lat1),
                Double.parseDouble(lon2),
                Double.parseDouble(lat2)
        );
    }
}
