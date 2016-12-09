package com.vasilkoff.easyvpnfree.util;

import com.vasilkoff.easyvpnfree.model.Server;

/**
 * Created by Kusenko on 09.12.2016.
 */

public class ConnectUtil {

    private static final String CONNECT_BAD = "ic_connect_bad";
    private static final String CONNECT_GOOD = "ic_connect_good";
    private static final String CONNECT_EXCELLENT = "ic_connect_excellent";

    public static String getConnectIcon(Server server) {
        int speed = Integer.parseInt(server.getSpeed());
        int sessions = Integer.parseInt(server.getNumVpnSessions());

        int ping = 0;
        if (!(server.getPing().equals("-") || server.getPing().equals(""))) {
            ping = Integer.parseInt(server.getPing());
        }

        if (speed > 10000000 && ping < 30 && (sessions != 0 && sessions < 100)) {
            return CONNECT_EXCELLENT;
        } else if (speed < 1000000 || ping > 100 || (sessions == 0 || sessions > 150)) {
            return CONNECT_BAD;
        } else {
            return CONNECT_GOOD;
        }
    }
}
