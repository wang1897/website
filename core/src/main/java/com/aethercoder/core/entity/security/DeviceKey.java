package com.aethercoder.core.entity.security;

import java.io.Serializable;

/**
 * Created by hepengfei on 25/12/2017.
 */
public class DeviceKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private String deviceId;
    private String appKey;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
}
