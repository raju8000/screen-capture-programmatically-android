package com.example.screencapture

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.hardware.Camera.PictureCallback
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cubi.casa.cubicapture.CubiCapture
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


//import java.util.*


class MainActivity : AppCompatActivity() {

    companion object{
        val DONE = 1
        val NEXT = 2
        val PERIOD = 1
    }
    val TAG : String = "Picubo"
    private var camera: Camera? = null
    private var cameraId = 0
    private val display: ImageView? = null
    private var timer: Timer? = null
    var previewHolder: SurfaceHolder? = null

    var cubi =CubiCapture

    private var ssError = false
    private var ssPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cubi.

        val cameraSurface = findViewById<CameraSurfaceView>(R.id.surfaceview)
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, 100)
        }*/

        val helloTxt = findViewById<TextView>(R.id.helloTxt)

        helloTxt.setOnClickListener {
            Log.e("","SOMETHING")
            takeScreenshotOld(null)
            /*val intent = Intent("android.media.action.IMAGE_CAPTURE")
            startActivity(intent)*/

            /*Handler().postDelayed(Runnable {

                val bitmap:Bitmap? = cameraSurface.bitmap
                if(bitmap!=null){
                    Log.d("", "Bitmap Nt null")
                    val image_view = findViewById<ImageView>(R.id.image_view)
                    image_view.setImageBitmap(bitmap)
                }
                                           }, 2000)*/

            /*val front_translucent = Intent(application
                .applicationContext, CameraService::class.java)
            front_translucent.putExtra("Front_Request", true)
            front_translucent.putExtra("Quality_Mode", 100)
            application.applicationContext.startService(front_translucent)*/
        }


    }


    private fun reloadMedia() {
        try {
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val file = File(ssPath)
            val uri = Uri.fromFile(file)
            intent.data = uri
            this.sendBroadcast(intent)
        } catch (ex: java.lang.Exception) {
            Log.println(Log.INFO, TAG, "Error reloading media lib: " + ex.message)
        }
    }

    ///Screen Related Short code
    private fun getScreenshotName(): String? {
        val sf = SimpleDateFormat("yyyyMMddHHmmss")
        val sDate = sf.format(Date())
        return "screenshot-$sDate.png"
    } // getScreenshotName()


    private fun getScreenshotPath(): String {
        val pathTemporary: String = this.cacheDir.path
        Log.println(Log.INFO, TAG, "path temporary: $pathTemporary")
        val dirPath = pathTemporary + "/" + getScreenshotName()
        Log.println(Log.INFO, TAG, "Built ScreeshotPath: $dirPath")
        return dirPath
    }

    private fun writeBitmap(bitmap: Bitmap): String? {
        try {
            val path: String = getScreenshotPath()
            val imageFile = File(path)
            val oStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream)
            oStream.flush()
            oStream.close()
            return path
        } catch (ex: java.lang.Exception) {
            Log.println(Log.INFO, TAG, "Error writing bitmap: " + ex.message)
        }
        return null
    }

    private fun takeScreenshotOld(viewx: View?) {
        Log.println(Log.INFO, TAG, "Trying to take screenshot [old way]")
        try {
            val view: View = window.decorView.rootView
            view.isDrawingCacheEnabled = true
            var bitmap: Bitmap? = null
            /*if (this.javaClass == FlutterView::class.java) {
                bitmap = (this as FlutterView).bitmap
            } else if (this.javaClass == FlutterRenderer::class.java) {
                bitmap = (this as FlutterRenderer).bitmap
            }*/
            bitmap = Bitmap.createBitmap(view.width,view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            if (bitmap == null) {
                this.ssError = true
                this.ssPath = null
                Log.println(Log.INFO, TAG, "The bitmap cannot be created :(")
                return
            } // if
            view.isDrawingCacheEnabled = false
            val image_view = findViewById<ImageView>(R.id.image_view)
            image_view.setImageBitmap(bitmap)
            /*val path: String? = writeBitmap(bitmap)
            if (path == null || path.isEmpty()) {
                this.ssError = true
                this.ssPath = null
                Log.println(Log.INFO, TAG, "The bitmap cannot be written, invalid path.")
                return
            } // if
            this.ssError = false
            this.ssPath = path
            reloadMedia()*/
        } catch (ex: Exception) {
            Log.println(Log.INFO, TAG, "Error taking screenshot: " + ex.message)
        }
    }
}