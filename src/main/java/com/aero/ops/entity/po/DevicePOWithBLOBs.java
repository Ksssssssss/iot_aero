package com.aero.ops.entity.po;

public class DevicePOWithBLOBs extends DevicePO {
    private String params;

    private String valueRecently;

    private String extra;

    private String html;

    private String devicevaluesettings;

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params == null ? null : params.trim();
    }

    public String getValueRecently() {
        return valueRecently;
    }

    public void setValueRecently(String valueRecently) {
        this.valueRecently = valueRecently == null ? null : valueRecently.trim();
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra == null ? null : extra.trim();
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html == null ? null : html.trim();
    }

    public String getDevicevaluesettings() {
        return devicevaluesettings;
    }

    public void setDevicevaluesettings(String devicevaluesettings) {
        this.devicevaluesettings = devicevaluesettings == null ? null : devicevaluesettings.trim();
    }
}