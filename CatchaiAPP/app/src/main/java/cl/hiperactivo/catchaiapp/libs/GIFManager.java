package cl.hiperactivo.catchaiapp.libs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hernanBeiza on 6/10/17.
 */

public class GIFManager extends AsyncTask<String, Void, Boolean> {

    public GIFDelegate delegate;
    public interface GIFDelegate {
        void onGIFEndOK();
        void onGIFEndWithError();
    }


    private static final String tag = "GIFManager";
    private static final String nombreCarpeta = "cl.hiperactivo.catchaiapp";

    private ArrayList<byte[]> imagenes;

    public GIFManager() { }

    public GIFManager(GIFDelegate delegate) {
        this.delegate = delegate;
    }

    public GIFDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(GIFDelegate delegate) {
        this.delegate = delegate;
    }

    public void guardarGIF(ArrayList<byte[]> bytesImage){
        Log.d(tag,"guardarGIF");
        this.imagenes = bytesImage;
        this.execute();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        Log.d(tag,"doInBackground");
        //Procesar data
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.setRepeat(0);
        encoder.start(bos);

        for (byte[] data : imagenes){
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            encoder.addFrame(bitmap);
        }
        encoder.setSize(800,600);
        encoder.finish();
        FileOutputStream outStream = null;

        try {
            outStream = new FileOutputStream(this.getCarpeta()+this.getNombreArchivo());
            outStream.write(bos.toByteArray());
            outStream.close();
            //Limpiar arreglo de bytes de las imágenes capturas
            this.imagenes = null;
            return true;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }

    }

    private String getNombreArchivo(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd_hh:mm:ss", Locale.getDefault());
        return File.separator + "gif_" + dateFormat.format(new Date()) + ".gif";
    }

    //This is run on the UI thread so you can do as you wish here
    protected void onPostExecute(Boolean result) {
        Log.d(tag,"onPostExecute");
        if(result) {
            if (delegate != null) {
                //return result to activity
                delegate.onGIFEndOK();
            }
        } else {
            if(delegate!=null){
                delegate.onGIFEndWithError();
            }
        }
    }


    /***
     * Carpeta en donde se guardan las imagenes
     * @return String de la ruta de la carpeta
     */
    public String getCarpeta(){
        String carpeta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/"+this.nombreCarpeta;
        return carpeta;
    }


}
