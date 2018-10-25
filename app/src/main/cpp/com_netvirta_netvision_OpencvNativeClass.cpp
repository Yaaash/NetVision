#include <jni.h>
#include "com_netvirta_netvision_OpencvNativeClass.h"

JNIEXPORT jint JNICALL Java_com_netvirta_netvision_OpencvNativeClass_convertGray
  (JNIEnv *, jclass, jlong addrRgba, jlong addrGray){
    Mat& mRgba = *(Mat*)addrRgba;
    Mat& mGray = *(Mat*)addrGray;

    int conv;
    jint retVal;
    cvtColor(mRgba, mGray, CV_RGBA2GRAY);
    if(mGray.rows == mRgba.rows && mGray.cols == mRgba.cols){
        conv = 1;
    }else {
        conv = 0;
    }
    retVal = (jint) conv;
    return retVal;
  }

