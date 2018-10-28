#include <jni.h>
#include "com_netvirta_netvision_OpencvNativeClass.h"

JNIEXPORT jint JNICALL Java_com_netvirta_netvision_OpencvNativeClass_convertGray
        (JNIEnv *, jclass, jlong addrRgba, jlong addrGray) {
    Mat &mRgba = *(Mat *) addrRgba;
    Mat &mGray = *(Mat *) addrGray;

    int conv;
    jint retVal;
    cvtColor(mRgba, mGray, CV_RGBA2GRAY);
    if (mGray.rows == mRgba.rows && mGray.cols == mRgba.cols) {
        conv = 1;
    } else {
        conv = 0;
    }
    retVal = (jint) conv;
    return retVal;
}

JNIEXPORT void JNICALL Java_com_netvirta_netvision_OpencvNativeClass_faceDetection
        (JNIEnv *, jclass, jlong rgbaAddress) {

    Mat &frame = *(Mat *) rgbaAddress;
    detect(frame);
}

void detect(Mat& frame){

    String face_cascade_name = "/sdcard/Download/cascade/haarcascade_frontalface_alt.xml";
    String eyes_cascade_name = "/sdcard/Download/cascade/haarcascade_eye_tree_eyeglasses.xml";
    //syslog(LOG_CRIT, "hello syslog");
    CascadeClassifier face_cascade;
    CascadeClassifier eyes_cascade;
    if( !face_cascade.load( face_cascade_name ) ){/* printf("--(!)Error loading\n")*/; return ; };
    if( !eyes_cascade.load( eyes_cascade_name ) ){/* printf("--(!)Error loading\n")*/; return ; };


    std::vector<Rect> faces;
    Mat frame_gray;

    cvtColor( frame, frame_gray, CV_BGR2GRAY );
    equalizeHist( frame_gray, frame_gray );

    //-- Detect faces
//    face_cascade.detectMultiScale( frame_gray, faces, 1.1, 2, 0|CV_HAAR_SCALE_IMAGE, Size(30, 30) );

    for( size_t i = 0; i < faces.size(); i++ )
    {
        Point center( faces[i].x + faces[i].width*0.5, faces[i].y + faces[i].height*0.5 );
        ellipse( frame, center, Size( faces[i].width*0.5, faces[i].height*0.5), 0, 0, 360, Scalar( 255, 0, 255 ), 4, 8, 0 );

        Mat faceROI = frame_gray( faces[i] );
        std::vector<Rect> eyes;

        //-- In each face, detect eyes
//        eyes_cascade.detectMultiScale( faceROI, eyes, 1.1, 2, 0 |CV_HAAR_SCALE_IMAGE, Size(30, 30) );

        for( size_t j = 0; j < eyes.size(); j++ )
        {
            Point center( faces[i].x + eyes[j].x + eyes[j].width*0.5, faces[i].y + eyes[j].y + eyes[j].height*0.5 );
            int radius = cvRound( (eyes[j].width + eyes[j].height)*0.25 );
            circle( frame, center, radius, Scalar( 255, 0, 0 ), 4, 8, 0 );
        }
    }

}

JNIEXPORT void JNICALL Java_com_netvirta_netvision_OpencvNativeClass_bodyDetection
        (JNIEnv *, jclass, jlong rgbaAddress){
    Mat& image = *(Mat *) rgbaAddress;

    String human_cascade_name = "/sdcard/Download/cascade/haarcascade_fullbody.xml";

    CascadeClassifier body_cascade;
    if( !body_cascade.load( human_cascade_name ) ){ /*printf("--(!)Error loading\n")*/; return ; };

    std::vector<Rect> bodies;
    Mat frame_gray;

    cvtColor( image, frame_gray, CV_BGR2GRAY );
    equalizeHist( frame_gray, frame_gray );

//    body_cascade.detectMultiScale( frame_gray, bodies, 1.1, 2, 0|CV_HAAR_SCALE_IMAGE, Size(30, 30) );

    for(int i = 0; i< bodies.size(); i++){
        rectangle(image, Point(bodies[i].x, bodies[i].y),
        Point(bodies[i].x + bodies[i].width, bodies[i].y + bodies[i].height), Scalar(0, 255, 30));
    }

}

JNIEXPORT void JNICALL Java_com_netvirta_netvision_OpencvNativeClass_lineDetection
        (JNIEnv *, jclass, jlong rgbaAddress) {

    Mat &frame = *(Mat *) rgbaAddress;

    Mat image_noise;
    GaussianBlur(frame, image_noise, cv::Size(3, 3), 0, 0);

    // edge detector
    Mat edge_output;
    Mat kernel;
    Point anchor;

    // Convert image from RGB to gray
    cv::cvtColor(image_noise, edge_output, cv::COLOR_RGB2GRAY);
    // Binarize gray image
    cv::threshold(edge_output, edge_output, 140, 255, cv::THRESH_BINARY);

    // Create the kernel [-1 0 1]
    // This kernel is based on the one found in the
    // Lane Departure Warning System by Mathworks
    anchor = Point(-1, -1);
    kernel = Mat(1, 3, CV_32F);
    kernel.at<float>(0, 0) = -1;
    kernel.at<float>(0, 1) = 0;
    kernel.at<float>(0, 2) = 1;

    // Filter the binary image to obtain the edges
    filter2D(edge_output, edge_output, -1, kernel, anchor, 0, cv::BORDER_DEFAULT);

    // mask edge output
    Mat mask_output;
    cv::Mat mask = cv::Mat::zeros(edge_output.size(), edge_output.type());
    cv::Point pts[4] = {
            cv::Point(210, 720),
            cv::Point(550, 450),
            cv::Point(717, 450),
            cv::Point(1280, 720)
    };

    // Create a binary polygon mask
    cv::fillConvexPoly(mask, pts, 4, cv::Scalar(255, 0, 0));
    // Multiply the edges image and the mask to get the output
    cv::bitwise_and(edge_output, mask, mask_output);

    // get lines
    std::vector<cv::Vec4i> line;

    // rho and theta are selected by trial and error
    HoughLinesP(mask_output, line, 1, CV_PI/180, 20, 20, 30);

    for (auto i : line) {
        cv::line(frame, cv::Point(i[0], i[1]), cv::Point(i[2], i[3]), cv::Scalar(0, 255, 0), 5, CV_AA);
    }
    return;
}
