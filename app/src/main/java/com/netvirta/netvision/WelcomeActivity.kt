package com.netvirta.netvision

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import kotlinx.android.synthetic.main.activity_welcome.*
import org.opencv.android.OpenCVLoader

/**
 * This being the launcher activity guides user to CameraActivity where he/she can scan various objects
 * features, faces or other elements.
 *
 * @author Yashika Katyal
 */
class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        startButton?.setOnClickListener {
            requestPermission()
        }
        // Example of a call to a native method
//        sample_text.text = stringFromJNI()
    }

    private fun showPermissionExplanation() {
        AlertDialog.Builder(this).run {
            title = getString(R.string.camera_permission_title)
            setMessage(getString(R.string.camera_permission_message))
            setNegativeButton(getString(R.string.nah)) { dialog, _ -> dialog?.dismiss() }
            setPositiveButton(getString(R.string.sure_go_ahead)) { dialog, _ ->
                dialog?.dismiss()
                ActivityCompat.requestPermissions(
                    this@WelcomeActivity,
                    arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION
                )
            }
            show()
        }
    }

    /**
     * Request User's permission for Camera before going to the next Activity.
     * If not allowed, user will be prompt with a message showing why this permission is
     * important to us.
     */
    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.CAMERA
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showPermissionExplanation()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION
                )
            }
        } else {
            // start next activity - as we already have permission.
            startActivity(Intent(this@WelcomeActivity, CameraActivity::class.java))
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startActivity(Intent(this@WelcomeActivity, CameraActivity::class.java))
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    // todo do nothing for now
                }
                return
            }

            else -> {
                // Ignore all other requests.
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {

        const val CAMERA_PERMISSION = 300

        init {
            // Used to load the 'native-lib' library on application startup.
            System.loadLibrary("native-lib")
            // Check if OpenCv is loaded or not
            // Todo : Remove these logs when pushed to LIVE
            if (OpenCVLoader.initDebug()) {
                Log.d("Welcome", "Successfully loaded")
            } else {
                Log.e("Welcome", "Failed to Load.")
            }
        }
    }
}
