package net.envigo.linterna;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

/* TODO
    Shake Mode (Sacudir para apagar o encender
    ¿desktop widget?
    ¿blink o SOS?
 */
/* Changelog

 */

public class MainActivity extends AppCompatActivity  {

    SharedPreferences prefs;
    public static final String My_Prefs = "FlashPrefs";

    ImageButton FlashButton;
    Spinner spinTimer;

    Switch AutoOnSwitch;

    private lantern flashlight;
    private oldLantern old_flashlight;

    private boolean isFlashOn = false;
    private boolean AutoOn = true;

    CountDownTimer TurnOffTimer;
    long timeTimer = 0;

    Context context;

    int ToastDuration = Toast.LENGTH_SHORT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FlashButton = findViewById(R.id.FlashButton);
        spinTimer = findViewById(R.id.spinTimer);
        AutoOnSwitch = findViewById(R.id.autoOn);

        if (savedInstanceState != null) {
            isFlashOn = savedInstanceState.getBoolean("isFlashOn");
        }
        context = getApplicationContext();

        prefs = getSharedPreferences(My_Prefs, MODE_PRIVATE);

        AutoOn = prefs.getBoolean("AutoOn", true);


        Log.d("Log", "onCreate");

        //SPINNER
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.flashTimer, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTimer.setAdapter(adapter);
        spinTimer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Turn off state: " + selectedItem, ToastDuration).show();

                Log.d("Log:", "isFlashOn=" + isFlashOn);

                if (selectedItem.contains("Off")) {
                    timeTimer = 0;
                    if(isFlashOn) cancelTimer();
                }
                if (selectedItem.contains("10s")) {
                    timeTimer = 10000;
                    Log.d("Log:", "10s select: " + timeTimer);
                    if(isFlashOn) startTimer(10000);
                }
                if (selectedItem.contains("20s")) {
                    timeTimer = 20000;
                    if(isFlashOn) startTimer(20000);
                }
                if (selectedItem.contains("30s")) {
                    timeTimer = 30000;
                    if(isFlashOn) startTimer(30000);
                }
                if (selectedItem.contains("1m")) {
                    timeTimer = 60000;
                    if(isFlashOn) startTimer(60000);
                }
                if (selectedItem.contains("5m")) {
                    timeTimer = 300000;
                    if(isFlashOn)  startTimer(300000);
                }

            }

            public void onNothingSelected(AdapterView<?> parent)
            {
            }

        });
        //END SPINNER

        //Flashlight

        if (!checkForCameraPermission(context)) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 100);
        }
        if (checkIfCameraFeatureExists(context)) {
            if (CameraVersion()) {
                Log.d("Log", "new api");
                flashlight = new lantern(context);
            } else {
                Log.d("Log", "old api");
                old_flashlight = new oldLantern(context);
            }
        } else {
            Toast.makeText(context, getString(R.string.NoFlash), ToastDuration).show();
        }



        FlashButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(checkForCameraPermission(context)) {
                    if (isFlashOn) {
                        cancelTimer();
                        turnOff();
                    } else {
                        rTimer();
                        turnOn();
                    }
                } else {
                    Toast.makeText(context, getString(R.string.NoPermiss), ToastDuration).show();
                    finish();
                }
            }

        });

        if (!checkForCameraPermission(context)) {
            AutoOn = false;
        }
        AutoOnSwitch.setChecked(AutoOn);


        AutoOnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("AutoOn", isChecked);
                editor.commit();
                Toast.makeText(context, getString(R.string.SavedPrefs), ToastDuration).show();
            }
        });

        if (AutoOn) {
            rTimer();
            turnOn();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Log", "onResume");
        if(isFlashOn) {
            FlashButton.setImageResource(R.drawable.yellow_button);
        } else {
            FlashButton.setImageResource(R.drawable.red_button);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
        Log.d("Log", "onDestroy");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putBoolean("isFlashOn", isFlashOn);

        super.onSaveInstanceState(savedInstanceState);
    }

    private void turnOff() {
        if (CameraVersion()) {
            flashlight.turnOff();
            FlashButton.setImageResource(R.drawable.red_button);
            Toast.makeText(context, getString(R.string.FlashOff), ToastDuration).show();
        } else {
            FlashButton.setImageResource(R.drawable.red_button);
            old_flashlight.turnOff();
            Toast.makeText(context, getString(R.string.FlashOff), ToastDuration).show();
        }
        isFlashOn = false;
    }

    private void turnOn() {
        if (CameraVersion()) {
            flashlight.turnOn();
            FlashButton.setImageResource(R.drawable.yellow_button);
            Toast.makeText(context, getString(R.string.FlashOn), ToastDuration).show();
        } else {
            old_flashlight.turnOn();
            FlashButton.setImageResource(R.drawable.yellow_button);
            Toast.makeText(context, getString(R.string.FlashOn), ToastDuration).show();
        }
        isFlashOn = true;
    }


    private boolean CameraVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private boolean checkForCameraPermission(Context context) {
        return context.checkCallingOrSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkIfCameraFeatureExists(Context context) {
        //    return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH); //New fail on old
        return true;
    }

    void rTimer() {

        if (TurnOffTimer != null) {
            cancelTimer();
        }
        Log.d("Log:", "rTimer called:" + timeTimer );
        startTimer(timeTimer);
    }

    void startTimer(long ms_time) {
        if (ms_time == 0) return;

        cancelTimer();

        TurnOffTimer = new CountDownTimer(ms_time, ms_time) {
            public void onTick(long millisUntilFinished) {}
            public void onFinish() {
                Log.d("Log", "Turn off timer");
                turnOff();
            }
        };
        Log.d("Log", "Timer started");
        TurnOffTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        Log.d("Log", "Cancel timer Called");
        if(TurnOffTimer != null)
            TurnOffTimer.cancel();
    }

}
