package com.vasilkoff.easyvpnfree.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Vasilkoff on 27/09/16.
 */

@DatabaseTable(tableName = "VPNGateServerRecords")
public class VPNGateServerRecord {
    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField
    public String HostName;

    @DatabaseField
    public String IP;

    @DatabaseField
    public String Score;

    @DatabaseField
    public String Ping;

    @DatabaseField
    public String Speed;

    @DatabaseField
    public String CountryLong;

    @DatabaseField
    public String CountryShort;

    @DatabaseField
    public String NumVpnSessions;

    @DatabaseField
    public String Uptime;

    @DatabaseField
    public String TotalUsers;

    @DatabaseField
    public String TotalTraffic;

    @DatabaseField
    public String LogType;

    @DatabaseField
    public String Operator;

    @DatabaseField
    public String Message;

    @DatabaseField
    public String OpenVPN_ConfigData_Base64;

    public VPNGateServerRecord() {}

    public VPNGateServerRecord(String[] line) {
        HostName = line[0];
        IP = line[1];
        Score = line[2];
        Ping = line[3];
        Speed = line[4];
        CountryLong = line[5];
        CountryShort = line[6];
        NumVpnSessions = line[7];
        Uptime = line[8];
        TotalUsers = line[9];
        TotalTraffic = line[10];
        LogType = line[11];
        Operator = line[12];
        Message = line[13];
        OpenVPN_ConfigData_Base64 = line[14];

    }
}
