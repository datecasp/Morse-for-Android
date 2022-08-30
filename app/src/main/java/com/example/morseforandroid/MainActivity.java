package com.example.morseforandroid;

import android.app.Activity;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity
{
    private CameraManager manager;
    private String cameraId;
    private CameraCharacteristics characteristics;
    private final boolean flashOn = false;
    private EditText et;
    private AlfabetoMorse.MorseBit[] mensaje = null;
    private CheckBox chkBucle;
    private boolean bucle = false;
    private Button btnCancelarBucle;
    private TareaAsincrona tareaBG;

    protected void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);

        et = findViewById(R.id.txtIntro);
        chkBucle = findViewById(R.id.chkBucle);
        btnCancelarBucle = findViewById(R.id.btnCancelar);
    }

    public void btnCancelarClick(View v){
        tareaBG.cancel(true);
    }

    public void btnEncenderClick(View v) {
        if(chkBucle.isChecked()){btnCancelarBucle.setVisibility(View.VISIBLE);bucle = true;}

        mensaje = AlfabetoMorse.pattern(et.getText().toString());
        manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            for (int i =0; i <= manager.getCameraIdList().length; i++) {
                CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(manager.getCameraIdList()[i]);
                boolean flashAvailable = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                if (flashAvailable) {
                    cameraId = manager.getCameraIdList()[i]; // Usually front camera is at 0 position and back camera is 1.
                }
            }
        } catch (Exception ex) {}
        tareaBG = new TareaAsincrona();//***************************************
        tareaBG.execute();//****************************************************

    }

    /*
        Tarea en segundo plano que muestra el mensaje a través del uso
        del flash de la cámara.
        AsyncTask está descatalogado. Buscar sistema para realizarlo
     */
    private class TareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            do {
                for (AlfabetoMorse.MorseBit bit : mensaje) {
                    switch (bit) {
                        case DOT:
                            try {
                                manager.setTorchMode(cameraId, true);
                                Thread.sleep(250);
                            } catch (Exception ex) {
                            }
                            break;
                        case DASH:
                            try {
                                manager.setTorchMode(cameraId, true);
                                Thread.sleep(500);
                            } catch (Exception ex) {
                            }
                            break;
                        case GAP:
                            try {
                                manager.setTorchMode(cameraId, false);
                                Thread.sleep(250);
                            } catch (Exception ex) {
                            }
                            break;
                        case WORD_GAP:
                            try {
                                manager.setTorchMode(cameraId, false);
                                Thread.sleep(750);
                            } catch (Exception ex) {
                            }
                            break;
                        case LETTER_GAP:
                            try {
                                manager.setTorchMode(cameraId, false);
                                Thread.sleep(500);
                            } catch (Exception ex) {
                            }
                            break;
                        default:
                            break;
                    }
                }
                try{manager.setTorchMode(cameraId,false);}catch(Exception ex){}
                try{Thread.sleep(1000);}catch(Exception ex){}
                if(isCancelled()){break;}
            }while(bucle);

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result)
                Toast.makeText(MainActivity.this, "Mensaje enviado", Toast.LENGTH_SHORT).show();
            bucle = false;
            btnCancelarBucle.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(MainActivity.this, "Mensaje cancelado", Toast.LENGTH_SHORT).show();
            bucle = false;
            btnCancelarBucle.setVisibility(View.INVISIBLE);
        }
    }
}