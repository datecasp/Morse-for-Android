package com.example.morseforandroid;

import android.app.Activity;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
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
    private AlfabetoMorse.MorseBit[] message = null;
    private CheckBox chkLoop;
    private boolean loopFlag = false;
    private Button btnCancelLoop;
    private boolean light = false, sound = false;
    //Flag to avoid sending a message while another message is in use
    private boolean sendingMessage = false;

    protected void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);

        et = findViewById(R.id.txtIntro);
        chkLoop = findViewById(R.id.chkLoop);
        btnCancelLoop = findViewById(R.id.btnCancelMsg);
    }

    /*
        Cancels loop messaging
        Changes boolean loopFlag flag and show toast with info
     */
    public void btnCancelClick(View v)
    {
        Toast.makeText(MainActivity.this, "Message canceled", Toast.LENGTH_SHORT).show();
        loopFlag = false;
        btnCancelLoop.setVisibility(View.INVISIBLE);
    }
    public void btnSendLightMessageClick(View v)
    {

        if (!sendingMessage)
        {
            light = true;
            sound = false;
            SendMessage(v);
        }
    }
    public void btnSendSoundMessageClick(View v)
    {

        if (!sendingMessage)
        {
            light = false;
            sound = true;
            SendMessage(v);
        }
    }
    public void btnSendLightandSoundMessageClick(View v)
    {
        if (!sendingMessage)
        {
            light = true;
            sound = true;
            SendMessage(v);
        }
 }
    public void SendMessage(View v)
    {
        //Beep for sound morse
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        //Parse message into Morse alphabet
        message = AlfabetoMorse.pattern(et.getText().toString());
        //Get CameraManager for flash use
        manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        //BG work objects
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        //Set flag SendingMessage to true
        sendingMessage = true;

        if (chkLoop.isChecked())
        {
            btnCancelLoop.setVisibility(View.VISIBLE);
            loopFlag = true;
        }

        /*
            Try to get camera manager for the use of flash light
         */
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
            //Write some exception info
        }

        /*
            Use Executor.execute() to handle the BG work
         */
        executor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                /*
                    Parsing text to morse symbols and loop if necessary

                    BG work

                */
                do
                {
                    for (AlfabetoMorse.MorseBit bit : message)
                    {
                        switch (bit)
                        {
                            case DOT:
                                try
                                {
                                    if(light) manager.setTorchMode(cameraId, true);
                                    if (sound) toneG.startTone(ToneGenerator.TONE_DTMF_0, 250); // 250 is duration in ms
                                    Thread.sleep(250);
                                } catch (Exception ex)
                                {
                                }
                                break;
                            case DASH:
                                try
                                {
                                    if(light) manager.setTorchMode(cameraId, true);
                                    if (sound) toneG.startTone(ToneGenerator.TONE_DTMF_0, 500); // 500 is duration in ms
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
                    /*
                        These two try blocks sets flash off and waits 1 second for the end
                        of the message (if looping it´s needed to separate lighting)
                     */
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
                    if (!loopFlag)
                    {
                        break;
                    }
                } while (loopFlag);


                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //UI post when BG work is done
                        Toast.makeText(MainActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                        loopFlag = false;
                        btnCancelLoop.setVisibility(View.INVISIBLE);
                        //Reset morse type flags
                        light = false;
                        sound = false;
                        sendingMessage = false;
                    }
                });
            }
        });
    }
}