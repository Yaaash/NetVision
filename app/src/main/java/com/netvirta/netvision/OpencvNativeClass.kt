package com.netvirta.netvision

object OpencvNativeClass {

    init {
        System.loadLibrary("my-opencv-lib")
    }

    external fun convertGray(matAddrRgba: Long, matAddrGray: Long): Int

    external fun faceDetection(rgbaAddress: Long)

    external fun bodyDetection(rgbaAddress: Long)

    external fun lineDetection(rgbaAddress: Long)
}
