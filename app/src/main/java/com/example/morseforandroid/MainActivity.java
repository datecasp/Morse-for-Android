package com.example.morseforandroid;

import android.app.Activity;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity
{
    private final boolean flashOn = false;
    private CameraManager manager;
    private String cameraId;
    private CameraCharacteristics characteristics;
    private EditText et;
    private AlfabetoMorse.MorseBit[] mensaje = null;
    private CheckBox chkBucle;
    private boolean bucle = false;
    private Button btnCancelarBucle;

    protected void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);

        et = findViewById(R.id.txtIntro);
        chkBucle = findViewById(R.id.chkBucle);
        btnCancelarBucle = findViewById(R.id.btnCancelar);
    }

    public void btnCancelarClick(View v)
    {
        Toast.makeText(MainActivity.this, "Mensaje cancelado", Toast.LENGTH_SHORT).show();
        bucle = false;
        btnCancelarBucle.setVisibility(View.INVISIBLE);
    }

    public void btnEncenderClick(View v)
    {
        if (chkBucle.isChecked())
        {
            btnCancelarBucle.setVisibility(View.VISIBLE);
            bucle = true;
        }

        mensaje = AlfabetoMorse.pattern(et.getText().toString());
        manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try
        {
            for (int i = 0; i <= manager.getCameraIdList().length; i++)
            {
                CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(manager.getCameraIdList()[i]);
                boolean flashAvailable = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                if (flashAvailable)
                {
                    cameraId = manager.getCameraIdList()[i]; // Usually front camera is at 0 position and back camera is 1.
                }
            }
        } catch (Exception ex)
        {
        }


        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                //BG work
                do
                {
                    for (AlfabetoMorse.MorseBit bit : mensaje)
                    {
                        switch (bit)
                        {
                            case DOT:
                                try
                                {
                                    manager.setTorchMode(cameraId, true);
                                    Thread.sleep(250);
                                } catch (Exception ex)
                                {
                                }
                                break;
                            case DASH:
                                try
                                {
                                    manager.setTorchMode(cameraId, true);
                                    Thread.sleep(500);
                                } catch (Exception ex)
                                {
                                }
                                break;
                            case GAP:
                                try
                                {
                                    manager.setTorchMode(cameraId, false);
                                    Thread.sleep(250);
                                } catch (Exception ex)
                                {
                                }
                                break;
                            case WORD_GAP:
                                try
                                {
                                    manager.setTorchMode(cameraId, false);
                                    Thread.sleep(750);
                                } catch (Exception ex)
                                {
                                }
                                break;
                            case LETTER_GAP:
                                try
                                {
                                    manager.setTorchMode(cameraId, false);
                                    Thread.sleep(500);
                                } catch (Exception ex)
                                {
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    try
                    {
                        manager.setTorchMode(cameraId, false);
                    } catch (Exception ex)
                    {
                    }
                    try
                    {
                        Thread.sleep(1000);
                    } catch (Exception ex)
                    {
                    }
                    if (!bucle)
                    {
                        break;
                    }
                } while (bucle);


                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //UI post
                        Toast.makeText(MainActivity.this, "Mensaje enviado", Toast.LENGTH_SHORT).show();
                        bucle = false;
                        btnCancelarBucle.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }
}