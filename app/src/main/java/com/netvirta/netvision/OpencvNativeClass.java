package com.netvirta.netvision;

public class OpencvNativeClass {

    static {
        System.loadLibrary("my-opencv-lib");
    }

    public native static int convertGray(long matAddrRgba, long matAddrGray);
}
