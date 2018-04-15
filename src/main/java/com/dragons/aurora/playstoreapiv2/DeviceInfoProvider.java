package com.dragons.aurora.playstoreapiv2;

public interface DeviceInfoProvider {

    AndroidCheckinRequest generateAndroidCheckinRequest();
    DeviceConfigurationProto getDeviceConfigurationProto();
    String getUserAgentString();
    String getAuthUserAgentString();
    int getSdkVersion();
    int getPlayServicesVersion();
    String getMccmnc();
}
