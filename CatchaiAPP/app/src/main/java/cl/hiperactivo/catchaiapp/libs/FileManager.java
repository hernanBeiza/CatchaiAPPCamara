package cl.hiperactivo.catchaiapp.libs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hernanBeiza on 6/10/17.
 */

public class FileManager extends AsyncTask <String, Void, Boolean> {

    public FIleManagerDelegate delegate;

    public interface FIleManagerDelegate {
        void onFileOK();
        void onFileError(String error);
    }

    private static final String tag = "FileManager";
    private static final String nombreCarpeta = "cl.hiperactivo.catchaiapp";

    private File saveFile;
    private byte[] camaraData;

    public void guardarFoto(byte[] camaraData){
        saveFile = openFileForImage();
        this.camaraData = camaraData;
        if (saveFile != null) {
            this.execute();
        } else {
            Log.d(tag, "No se puede abrir la ubicación de archivo");
            this.delegate.onFileError("No se puede abrir la ubicación de archivo");
        }
    }

    public static void obtenerFotos(){
        File imageDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),nombreCarpeta);
        Log.d(tag,imageDirectory.toString());
        File[] files = imageDirectory.listFiles();
        Log.d(tag,"Files Size: "+ files.length);
        for (int i = 0; i < files.length; i++) {
            Log.d("Files", "FileName:" + files[i].getName());
        }
    }

    // your background code here. Don't touch any UI components
    @Override
    protected Boolean doInBackground(String... params) {
        Log.d(tag,"doInBackground");
        if (camaraData != null) {
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(saveFile);
                Bitmap mCameraBitmap = null;
                if (camaraData != null) {
                    mCameraBitmap = BitmapFactory.decodeByteArray(camaraData, 0, camaraData.length);
                }
                if (!mCameraBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)) {
                    Log.d(tag, "Unable to save image to file.");
                    outStream.close();
                    return false;
                } else {
                    Log.d(tag, "Saved image to: " + saveFile.getPath());
                    outStream.close();
                    return true;
                }
            } catch (Exception e) {
                Log.d(tag, "Unable to save image to file.");
                return false;
            }
        }
        return false;
    }

    //This is run on the UI thread so you can do as you wish here
    protected void onPostExecute(Boolean result) {
        Log.d(tag,"onPostExecute");
        if(result) {
            this.delegate.onFileOK();
        } else {
            this.delegate.onFileError("Error al guardar el archivo");
        }
    }

    private File openFileForImage() {
        Log.d(tag,"openFileForImage");

        File imageDirectory = null;
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            imageDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),nombreCarpeta);

            if (!imageDirectory.exists() && !imageDirectory.mkdirs()) {
                imageDirectory = null;
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd_hh:mm:ss", Locale.getDefault());
                return new File(imageDirectory.getPath() + File.separator + "image_" + dateFormat.format(new Date()) + ".png");
            }
        }
        return null;
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
