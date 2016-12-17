package com.vasilkoff.easyvpnfree.util;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import de.blinkt.openvpn.core.OpenVPNService;

/**
 * Created by Kusenko on 16.12.2016.
 */

public class TotalTraffic {

    public static String TRAFFIC_ACTION = "traffic_action";

    public static String DOWNLOAD_ALL = "download_all";
    public static String DOWNLOAD_SESSION = "download_session";
    public static String UPLOAD_ALL = "upload_all";
    public static String UPLOAD_SESSION = "upload_session";


    public static void calcTraffic(Context context, long in, long out, String diffInStr, String diffOutStr) {
        List<String> totalTraffic = getTotalTraffic(in, out);

        Intent traffic = new Intent();
        traffic.setAction(TRAFFIC_ACTION);
        traffic.putExtra(DOWNLOAD_ALL, totalTraffic.get(0));
        traffic.putExtra(DOWNLOAD_SESSION , diffInStr);
        traffic.putExtra(UPLOAD_ALL , totalTraffic.get(1));
        traffic.putExtra(UPLOAD_SESSION , diffOutStr);
        context.sendBroadcast(traffic);
    }

    public static List<String> getTotalTraffic() {
        return getTotalTraffic(0, 0);
    }

    public static List<String> getTotalTraffic(long in, long out) {
        List<String> totalTraffic = new ArrayList<String>();
        long inTotal =  PropertiesService.getDownloaded();
        long outTotal =  PropertiesService.getUploaded();

        inTotal = inTotal + in;
        PropertiesService.setDownloaded(inTotal );
        outTotal = outTotal + out;
        PropertiesService.setUploaded(outTotal);

        totalTraffic.add(OpenVPNService.humanReadableByteCount(inTotal, true));
        totalTraffic.add(OpenVPNService.humanReadableByteCount(outTotal, true));

        return totalTraffic;
    }
}
