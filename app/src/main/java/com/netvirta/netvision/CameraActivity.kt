package com.netvirta.netvision

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.SurfaceView
import kotlinx.android.synthetic.main.activity_camera.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.CvType
import org.opencv.core.Mat

/**
 * This activity shows the constant feed of the camera where:
 * - Option to change the orientation
 * - Feed ends when you press the end button
 * - It detects major or minor features and display on the users screen.
 *
 * @author Yashika Katyal
 */
class CameraActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener {

    private var callbackLoader: BaseLoaderCallback = MyLoaderCallback(this@CameraActivity)
    private var rgbaMat: Mat? = null
    private var greyMat: Mat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // enable Camera View
        javaCameraView.visibility = SurfaceView.VISIBLE
        javaCameraView.setCvCameraViewListener(this)
    }

    override fun onResume() {
        super.onResume()
        if (OpenCVLoader.initDebug()) {
            // If successfully loaded connect the loader with success
            Log.d(CameraActivity::class.java.simpleName, "Successfully loads")
            callbackLoader.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        } else {
            // If failed to load OpenCv, retry with async of OpenCVLoader
            Log.e(CameraActivity::class.java.simpleName, "Not loaded")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, callbackLoader)
        }
    }

    /**
     * This is a callback class for OpenCVLoader to enable or disable CameraView
     */
    inner class MyLoaderCallback(context: Context) : BaseLoaderCallback(context) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                BaseLoaderCallback.SUCCESS -> javaCameraView?.enableView()
                else -> super.onManagerConnected(status)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Disable view on onPause as we don't want to keep it enable when user is not on the Activity
        javaCameraView?.disableView()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Disable view on onDestroy as we don't want to keep it enable when user is not on the Activity
        javaCameraView?.disableView()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        rgbaMat = Mat(height, width, CvType.CV_8UC4)
        greyMat = Mat(height, width, CvType.CV_8UC1)
    }

    override fun onCameraViewStopped() {
        rgbaMat?.release()
    }

    override fun onCameraFrame(inputFrame: Mat?): Mat {
        rgbaMat = inputFrame
        if (rgbaMat == null || greyMat == null) {
            return rgbaMat!!
        }
        OpencvNativeClass.convertGray(rgbaMat?.nativeObjAddr?:0, greyMat?.nativeObjAddr?:0)
        return greyMat!!
    }

}
