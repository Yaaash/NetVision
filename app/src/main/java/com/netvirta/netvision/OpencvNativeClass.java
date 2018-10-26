package com.netvirta.netvision;

public class OpencvNativeClass {

    static {
        System.loadLibrary("my-opencv-lib");
    }

    public native static int convertGray(long matAddrRgba, long matAddrGray);

    public native static void faceDetection(long rgbaAddress);

    public native static void bodyDetection(long rgbaAddress);
}
