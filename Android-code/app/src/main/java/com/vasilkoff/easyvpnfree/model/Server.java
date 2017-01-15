package com.vasilkoff.easyvpnfree.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kusenko on 29.09.2016.
 */

public class Server implements Parcelable {

    private String hostName;
    private String ip;
    private String score;
    private String ping;
    private String speed;
    private String countryLong;
    private String countryShort;
    private String numVpnSessions;
    private String uptime;
    private String totalUsers;
    private String totalTraffic;
    private String logType;
    private String operator;
    private String message;
    private String configData;
    private int quality;
    private String city;
    private int type;
    private String regionName;
    private double lat;
    private double lon;

    public Server(String hostName, String ip, String score, String ping, String speed, String countryLong, String countryShort, String numVpnSessions, String uptime, String totalUsers, String totalTraffic, String logType, String operator, String message, String configData, int quality, String city, int type, String regionName, double lat, double lon) {
        this.hostName = hostName;
        this.ip = ip;
        this.score = score;
        this.ping = ping;
        this.speed = speed;
        this.countryLong = countryLong;
        this.countryShort = countryShort;
        this.numVpnSessions = numVpnSessions;
        this.uptime = uptime;
        this.totalUsers = totalUsers;
        this.totalTraffic = totalTraffic;
        this.logType = logType;
        this.operator = operator;
        this.message = message;
        this.configData = configData;
        this.quality = quality;
        this.city = city;
        this.type = type;
        this.regionName = regionName;
        this.lat = lat;
        this.lon = lon;
    }

    protected Server(Parcel in) {
        hostName = in.readString();
        ip = in.readString();
        score = in.readString();
        ping = in.readString();
        speed = in.readString();
        countryLong = in.readString();
        countryShort = in.readString();
        numVpnSessions = in.readString();
        uptime = in.readString();
        totalUsers = in.readString();
        totalTraffic = in.readString();
        logType = in.readString();
        operator = in.readString();
        message = in.readString();
        configData = in.readString();
        quality = in.readInt();
        city = in.readString();
        type = in.readInt();
        regionName = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
    }

    public static final Creator<Server> CREATOR = new Creator<Server>() {
        @Override
        public Server createFromParcel(Parcel in) {
            return new Server(in);
        }

        @Override
        public Server[] newArray(int size) {
            return new Server[size];
        }
    };

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getPing() {
        return ping;
    }

    public void setPing(String ping) {
        this.ping = ping;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getCountryLong() {
        return countryLong;
    }

    public void setCountryLong(String countryLong) {
        this.countryLong = countryLong;
    }

    public String getCountryShort() {
        return countryShort;
    }

    public void setCountryShort(String countryShort) {
        this.countryShort = countryShort;
    }

    public String getNumVpnSessions() {
        return numVpnSessions;
    }

    public void setNumVpnSessions(String numVpnSessions) {
        this.numVpnSessions = numVpnSessions;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public String getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(String totalUsers) {
        this.totalUsers = totalUsers;
    }

    public String getTotalTraffic() {
        return totalTraffic;
    }

    public void setTotalTraffic(String totalTraffic) {
        this.totalTraffic = totalTraffic;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getConfigData() {
        return configData;
    }

    public void setConfigData(String configData) {
        this.configData = configData;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hostName);
        dest.writeString(ip);
        dest.writeString(score);
        dest.writeString(ping);
        dest.writeString(speed);
        dest.writeString(countryLong);
        dest.writeString(countryShort);
        dest.writeString(numVpnSessions);
        dest.writeString(uptime);
        dest.writeString(totalUsers);
        dest.writeString(totalTraffic);
        dest.writeString(logType);
        dest.writeString(operator);
        dest.writeString(message);
        dest.writeString(configData);
        dest.writeInt(quality);
        dest.writeString(city);
        dest.writeInt(type);
        dest.writeString(regionName);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
    }
}
