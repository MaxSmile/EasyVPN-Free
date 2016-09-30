package com.vasilkoff.easyvpnfree.model;

/**
 * Created by Kusenko on 29.09.2016.
 */

public class Server {

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

    public Server(String hostName, String ip, String score, String ping, String speed, String countryLong, String countryShort, String numVpnSessions, String uptime, String totalUsers, String totalTraffic, String logType, String operator, String message, String configData) {
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
    }

    public String getHostName() {
        return hostName;
    }

    public String getIp() {
        return ip;
    }

    public String getScore() {
        return score;
    }

    public String getPing() {
        return ping;
    }

    public String getSpeed() {
        return speed;
    }

    public String getCountryLong() {
        return countryLong;
    }

    public String getCountryShort() {
        return countryShort;
    }

    public String getNumVpnSessions() {
        return numVpnSessions;
    }

    public String getUptime() {
        return uptime;
    }

    public String getTotalUsers() {
        return totalUsers;
    }

    public String getTotalTraffic() {
        return totalTraffic;
    }

    public String getLogType() {
        return logType;
    }

    public String getOperator() {
        return operator;
    }

    public String getMessage() {
        return message;
    }

    public String getConfigData() {
        return configData;
    }
}
