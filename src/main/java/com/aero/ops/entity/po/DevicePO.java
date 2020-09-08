package com.aero.ops.entity.po;

import java.math.BigDecimal;

public class DevicePO {
    private Integer id;

    private String uuid;

    private String sensorType;

    private String timeJoin;

    private String timeRecently;

    private String sensorName;

    private String status;

    private String mac;

    private String groupId;

    private String avatar;

    private String mongoTable;

    private String projectUuid;

    private String isstopped;

    private Integer secondchecktime;

    private String startvalue;

    private Integer ruleid;

    private String serialnum;

    private String funcs;

    private BigDecimal realrate;

    private Integer visible;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType == null ? null : sensorType.trim();
    }

    public String getTimeJoin() {
        return timeJoin;
    }

    public void setTimeJoin(String timeJoin) {
        this.timeJoin = timeJoin == null ? null : timeJoin.trim();
    }

    public String getTimeRecently() {
        return timeRecently;
    }

    public void setTimeRecently(String timeRecently) {
        this.timeRecently = timeRecently == null ? null : timeRecently.trim();
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName == null ? null : sensorName.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac == null ? null : mac.trim();
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId == null ? null : groupId.trim();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar == null ? null : avatar.trim();
    }

    public String getMongoTable() {
        return mongoTable;
    }

    public void setMongoTable(String mongoTable) {
        this.mongoTable = mongoTable == null ? null : mongoTable.trim();
    }

    public String getProjectUuid() {
        return projectUuid;
    }

    public void setProjectUuid(String projectUuid) {
        this.projectUuid = projectUuid == null ? null : projectUuid.trim();
    }

    public String getIsstopped() {
        return isstopped;
    }

    public void setIsstopped(String isstopped) {
        this.isstopped = isstopped == null ? null : isstopped.trim();
    }

    public Integer getSecondchecktime() {
        return secondchecktime;
    }

    public void setSecondchecktime(Integer secondchecktime) {
        this.secondchecktime = secondchecktime;
    }

    public String getStartvalue() {
        return startvalue;
    }

    public void setStartvalue(String startvalue) {
        this.startvalue = startvalue == null ? null : startvalue.trim();
    }

    public Integer getRuleid() {
        return ruleid;
    }

    public void setRuleid(Integer ruleid) {
        this.ruleid = ruleid;
    }

    public String getSerialnum() {
        return serialnum;
    }

    public void setSerialnum(String serialnum) {
        this.serialnum = serialnum == null ? null : serialnum.trim();
    }

    public String getFuncs() {
        return funcs;
    }

    public void setFuncs(String funcs) {
        this.funcs = funcs == null ? null : funcs.trim();
    }

    public BigDecimal getRealrate() {
        return realrate;
    }

    public void setRealrate(BigDecimal realrate) {
        this.realrate = realrate;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }
}