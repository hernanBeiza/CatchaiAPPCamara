package cl.hiperactivo.catchaiapp.Controllers.CamaraController;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import cl.hiperactivo.catchaiapp.R;

import cl.hiperactivo.catchaiapp.libs.GIFManager;

public class CamaraActivity extends Activity implements PictureCallback, SurfaceHolder.Callback, GIFManager.GIFDelegate {

    private static final String tag = "CamaraActivity";

    private static final String KEY_IS_CAPTURING = "is_capturing";

    private Camera mCamera;
    private ImageView mCameraImage;
    private SurfaceView mCameraPreview;
    private Button capturarButton;

    private byte[] mCameraData;
    private boolean isCapturing;
    private int fotograma = 1;
    private ArrayList<byte[]> imagenes;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);

        mCameraImage = (ImageView) findViewById(R.id.camera_image_view);
        mCameraImage.setVisibility(View.INVISIBLE);

        mCameraPreview = (SurfaceView) findViewById(R.id.preview_view);
        final SurfaceHolder surfaceHolder = mCameraPreview.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        capturarButton = (Button) findViewById(R.id.capturarButton);
        capturarButton.setOnClickListener(mCaptureImageButtonClickListener);
        final Button previsualizarButton = (Button) findViewById(R.id.previsualizarButton);
        previsualizarButton.setOnClickListener(onPrevisualizarGIFClickListener);

        isCapturing = true;
        this.imagenes = new ArrayList<byte[]>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                mCamera.setPreviewDisplay(mCameraPreview.getHolder());
                if (isCapturing) {
                    this.setupImageCapture();
                }
            } catch (Exception e) {
                Toast.makeText(CamaraActivity.this, "No se puede abrir la cámara ¿Error de permisos?", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private OnClickListener mCaptureImageButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(tag,"mCaptureImageButtonClickListener");
            captureImage();
        }
    };

    // Falta implementar una forma de leer el archivo GIF y mostrarlo en pantalla
    private OnClickListener onPrevisualizarGIFClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        Log.d(tag,"onPrevisualizarGIFClickListener");
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //savedInstanceState.putBoolean(KEY_IS_CAPTURING, isCapturing);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        /*
        isCapturing = savedInstanceState.getBoolean(KEY_IS_CAPTURING, mCameraData == null);
        Log.d(tag,"onRestoreInstanceState " + String.valueOf(isCapturing));
        if (mCameraData == null) {
            setupImageCapture();
        }
        */
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.d(tag,"onPictureTaken");
        Log.d(tag,String.valueOf(this.fotograma));
        mCameraData = data;

        //Guardar imágenes en disco si es que fuera necesario
        //FileManager manager = new FileManager();
        //manager.guardarFoto(mCameraData);

        //Para poder crear el gif con la data más tarde
        this.imagenes.add(mCameraData);
        fotograma++;

        //Reiniciar controles
        this.setupImageCapture();

        if(fotograma==4){
            fotograma = 1;
            procesarGIF();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(tag,"surfaceChanged");
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
                if (isCapturing) {
                    mCamera.startPreview();
                }
            } catch (IOException e) {
                Toast.makeText(CamaraActivity.this, "Unable to start camera preview.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) { }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) { }

    private void captureImage() {
        mCamera.takePicture(null, null, this);
    }

    private void procesarGIF() {
        Log.d(tag,"procesarGIF");
        this.isCapturing = false;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Atención");
        alertDialog.setMessage("Creando GIF...");

        this.dialog = alertDialog.create();
        this.dialog.show();

        this.capturarButton.setEnabled(false);
        capturarButton.setVisibility(View.INVISIBLE);
        this.mCamera.stopPreview();

        GIFManager gif = new GIFManager();
        gif.setDelegate(this);
        gif.guardarGIF(this.imagenes);
    }

    /**
     * Delegados de GIFManager
     */

    @Override
    public void onGIFEndOK() {
        Log.d(tag,"onGIFEndOK");
        this.fotograma = 1;
        this.dialog.cancel();
        this.setupImageCapture();
    }

    @Override
    public void onGIFEndWithError() {
        Log.d(tag,"onGIFEndWithError");
        this.fotograma = 1;
        this.dialog.cancel();
        this.setupImageCapture();
    }

    /***
     * Captura de imagen
     */
    private void setupImageCapture() {
        Log.d(tag,"setupImageCapture");

        capturarButton.setVisibility(View.VISIBLE);
        this.capturarButton.setEnabled(true);
        this.capturarButton.setText("Capturar " + String.valueOf(fotograma));

        mCameraImage.setVisibility(View.INVISIBLE);
        mCameraPreview.setVisibility(View.VISIBLE);
        mCamera.startPreview();
    }



}
