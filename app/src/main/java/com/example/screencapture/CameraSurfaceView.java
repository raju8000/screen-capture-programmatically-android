package com.example.screencapture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraSurfaceView";

    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera = null;
    private Bitmap mBitmap;
    private Context mContext;
    private Camera.Parameters mParameters;
    private byte[] byteArray;
    private List<Camera.Size> mSupportedPreviewSizes;
    private Camera.Size mPreviewSize;

    public CameraSurfaceView (Context context) {
        this(context, null);
    }

    public CameraSurfaceView (Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraSurfaceView (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;

        try {
            mSurfaceHolder = getHolder();
            mSurfaceHolder.addCallback(this);
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG," error CameraSurfaceView");
        }
    }

    @Override
    public void surfaceCreated(final SurfaceHolder surfaceHolder) {
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
            } catch (RuntimeException ignored) {
                Log.d("","Camera open error");
            }
        }

        try {
            if (mCamera != null) {
                WindowManager winManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                mCamera.setPreviewDisplay(mSurfaceHolder);
            }
        } catch (Exception e) {
            if (mCamera != null)
                mCamera.release();
            mCamera = null;
        }

        if (mCamera == null) {
            return;
        } else {
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] bytes, Camera camera) {
                    if (mParameters == null)
                    {
                        return;
                    }
                    byteArray = bytes;
                }
            });
        }

        setWillNotDraw(false);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        try {
            mParameters = mCamera.getParameters();

            List<Camera.Size> cameraSize = mParameters.getSupportedPreviewSizes();
            mPreviewSize = cameraSize.get(0);

            for (Camera.Size s : cameraSize) {
                if ((s.width * s.height) > (mPreviewSize.width * mPreviewSize.height)) {
                    mPreviewSize = s;
                }
            }

            mParameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            mCamera.setParameters(mParameters);
            mCamera.startPreview();

        } catch (Exception e) {
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
        }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public Bitmap getBitmap() {
        Log.d("","In Bitmap");
        try {
            if (mParameters == null)
                return null;

            if (mPreviewSize == null)
                return null;

            Log.d("","mParameters not null");
            int format = mParameters.getPreviewFormat();
            YuvImage yuvImage = new YuvImage(byteArray, format, mPreviewSize.width, mPreviewSize.height, null);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            Log.d("","yuvImage image taken");
            Rect rect = new Rect(0, 0, mPreviewSize.width, mPreviewSize.height);

            yuvImage.compressToJpeg(rect, 75, byteArrayOutputStream);
            Log.d("","yuvImage Compreshed");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable = true;
            options.inInputShareable = true;
            mBitmap = BitmapFactory.decodeByteArray(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.size(), options);

            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            Log.d("Error on ",ioe.toString());
        }

        return mBitmap;
    }

    public Camera getCamera() {
        return mCamera;
    }
}
