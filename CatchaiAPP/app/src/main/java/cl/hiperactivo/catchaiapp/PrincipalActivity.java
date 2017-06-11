package cl.hiperactivo.catchaiapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cl.hiperactivo.catchaiapp.Controllers.CamaraController.CamaraActivity;
import cl.hiperactivo.catchaiapp.libs.FileManager;

public class PrincipalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FileManager.obtenerFotos();
    }

    public void onCapturar(View v) {

        Intent camaraIntent = new Intent(this,CamaraActivity.class);
        startActivity(camaraIntent);

    }


}
