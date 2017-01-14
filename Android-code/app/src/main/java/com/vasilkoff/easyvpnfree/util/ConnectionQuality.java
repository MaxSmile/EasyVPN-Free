package com.vasilkoff.easyvpnfree.util;

import com.vasilkoff.easyvpnfree.model.Server;

/**
 * Created by Kusenko on 09.12.2016.
 */

public class ConnectionQuality {

    private static final String CONNECT_BAD = "ic_connect_bad";
    private static final String CONNECT_GOOD = "ic_connect_good";
    private static final String CONNECT_EXCELLENT = "ic_connect_excellent";
    private static final String CONNECT_INACTIVE = "ic_connect_inactive";

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
