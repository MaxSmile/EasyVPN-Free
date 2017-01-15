package com.vasilkoff.easyvpnfree.util;

/**
 * Created by Kusenko on 09.12.2016.
 */

public class ConnectionQuality {

    private static final String CONNECT_BAD = "ic_connect_bad";
    private static final String CONNECT_GOOD = "ic_connect_good";
    private static final String CONNECT_EXCELLENT = "ic_connect_excellent";
    private static final String CONNECT_INACTIVE = "ic_connect_inactive";

    private static final String POINT_BAD = "ic_point_red";
    private static final String POINT_GOOD = "ic_point_yellow";
    private static final String POINT_EXCELLENT = "ic_point_green";
    private static final String POINT_INACTIVE = "ic_point_grey";

    private static final String SIMPLE_POINT_BAD = "ic_simple_point_red";
    private static final String SIMPLE_POINT_GOOD = "ic_simple_point_yellow";
    private static final String SIMPLE_POINT_EXCELLENT = "ic_simple_point_green";
    private static final String SIMPLE_POINT_INACTIVE = "ic_simple_point_grey";

    public static String getConnectIcon(int quality) {
        switch (quality) {
            case 0:
                return CONNECT_INACTIVE;
            case 1:
                return CONNECT_BAD;
            case 2:
                return CONNECT_GOOD;
            case 3:
                return CONNECT_EXCELLENT;
            default:
                return CONNECT_INACTIVE;
        }
    }

    public static String getPointIcon(int quality) {
        switch (quality) {
            case 0:
                return POINT_INACTIVE;
            case 1:
                return POINT_BAD;
            case 2:
                return POINT_GOOD;
            case 3:
                return POINT_EXCELLENT;
            default:
                return POINT_INACTIVE;
        }
    }

    public static String getSimplePointIcon(int quality) {
        switch (quality) {
            case 0:
                return SIMPLE_POINT_INACTIVE;
            case 1:
                return SIMPLE_POINT_BAD;
            case 2:
                return SIMPLE_POINT_GOOD;
            case 3:
                return SIMPLE_POINT_EXCELLENT;
            default:
                return SIMPLE_POINT_INACTIVE;
        }
    }

    public static int getConnectionQuality(String speedStr, String sessionsStr, String pingStr) {

        int speed = Integer.parseInt(speedStr);
        int sessions = Integer.parseInt(sessionsStr);

        int ping = 0;
        if (!(pingStr.equals("-") || pingStr.equals(""))) {
            ping = Integer.parseInt(pingStr);
        }

        if (speed > 10000000 && ping < 30 && (sessions != 0 && sessions < 100)) {
            return 3;
        } else if (speed < 1000000 || ping > 100 || (sessions == 0 || sessions > 150)) {
            return 1;
        } else {
            return 2;
        }
    }
}
