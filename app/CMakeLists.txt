# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

add_library(my-opencv-lib SHARED D:/StudioProjects/NetVision/app/src/main/cpp/com_netvirta_netvision_OpencvNativeClass.cpp)

set(CMAKE_VERBOSE_MAKEFILE on)
add_library(lib_opencv SHARED IMPORTED)
set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION D:/StudioProjects/NetVision/openCVLibrary343/src/main/jniLibs/${ANDROID_ABI}/libopencv_java3.so)

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

target_link_libraries( # Specifies the target library.
        my-opencv-lib
        lib_opencv
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})

include_directories(C:/OpenCV-android-sdk/sdk/native/jni/include)
include_directories(C:/OpenCV-android-sdk/sdk/native/staticlibs)
