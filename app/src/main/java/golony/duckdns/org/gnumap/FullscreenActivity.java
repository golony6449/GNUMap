package golony.duckdns.org.gnumap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
//import golony.duckdns.org.gnumap.DBHelper;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements SensorEventListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    // 센서 메니저 생성
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagneticField;

    // 센서 값
    float[] magVal;
    float[] gravityVal;
    float mAzimut, mPitch, mRoll;
    double X, Y;
    float[] azimutList = new float[50];
    float[] rollList = new float[50];
    int count = 0;

    // DB Helper 생성
    DBHelper dbHelper;
    ArrayList<Building> buildList;

    // 디버깅용 TextView
    TextView text;
    TextView GPStext;
    BuildView markerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        // DB 초기화 파트
        dbHelper = new DBHelper(getApplicationContext(), Environment.getExternalStorageDirectory().getPath() + "/gnumap/main.db"
                , null, 1);
        System.out.println("객체 생성 OK");
        System.out.println("DB path: " + Environment.getExternalStorageDirectory().getPath() + "/gnumap/main.db");
        // 테스트
//        ArrayList<Building> temp = dbHelper.getResult();
//        dbHelper.searchName("컴퓨터과학관");
//        dbHelper.searchNum(30);
//        dbHelper.searchPos(35.152, 128.1, 0.001);


        // 센서 초기화 파트
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // GPS 초기화
        startLocationService();

        final CameraSurfaceView cameraView = new CameraSurfaceView(getApplicationContext());
        FrameLayout previewFrame = (FrameLayout) findViewById(R.id.previewFrame);
        previewFrame.addView(cameraView);

        text = new TextView(this);
        text.setTextColor(Color.WHITE);
        text.setText("테스트");
        previewFrame.addView(text);

        GPStext = new TextView(this);
        GPStext.setTextColor(Color.WHITE);
        GPStext.setText("\n\n\n수신중");
        previewFrame.addView(GPStext);

//        CompassView markerView = new CompassView(this);
//        previewFrame.addView(markerView);
//        markerView.invalidate();

        markerView = new BuildView(this);
        previewFrame.addView(markerView);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void onSensorChanged(SensorEvent event) {
//        System.out.println("센서 값 변경");
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magVal = event.values;
//           System.out.println(magVal[0]); // 0 ~ 2
//            text.setText("X: " + magVal[0] + " Y: " + magVal[1] + " Z: " + magVal[2]);
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravityVal = event.values;
        }

        if (magVal != null && gravityVal != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, gravityVal, magVal);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                mAzimut = (float) Math.toDegrees(orientation[0])+90+7; // 오차보정
                mPitch = (float) Math.toDegrees(orientation[1]);
                mRoll = (float) Math.toDegrees(orientation[2]);

                if (mAzimut < 0){
                    mAzimut = mAzimut + 360;
                }

                String result;
                result = "Azimut:" + mAzimut + "\n" + "Pitch:" + mPitch + "\n" + "Roll:" + mRoll;

                int idx = 20;
                if (count == idx - 1){
                    float azimutSum = 0;
                    float rollSum = 0;
                    for (int i = 0; i < idx; i++){
                        azimutSum += azimutList[i] / idx;
                        rollSum += rollList[i] / idx;
                    }
                    text.setText(result);
                    markerView.setSensorValue(azimutSum, rollSum);  // 기기가 90도(PI/2)만큼 회전되어있기 때문에 센서의 Roll 값을 Pitch로 사용
                    count = 0;
                }
                else{
                    azimutList[count] = mAzimut;
                    rollList[count] = mRoll;
                    count += 1;
                }

            }
        }
    }

    public void onAccuracyChanged (Sensor sensor,int val){

    }

    // GPS
    private void startLocationService() {
        System.out.println("위치서비스 시작");
        // 위치 관리자 객체 참조
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        // 위치 정보를 받을 리스너 생성
        GPSListener gpsListener = new GPSListener();
        long minTime = 100;
        float minDistance = 0;

        //
        // GPS를 이용한 위치 요청
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);

        // 위치 확인이 안되는 경우에도 최근에 확인된 위치 정보 먼저 확인
//        try {
//            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if (lastLocation != null) {
//
//                Double latitude = lastLocation.getLatitude();
//                Double longitude = lastLocation.getLongitude();
//                System.out.println("Last Known Location : " + " Latitude : " + latitude + " Longitude:" + longitude);
//                GPStext.setText("\n\n\n\nLast Known Location : " + " Latitude : " + latitude + " Longitude:" + longitude);
////                Toast.makeText(getApplicationContext(), "Last Known Location : " + "Latitude : " + latitude + "\nLongitude:" + longitude, Toast.LENGTH_LONG).show();
//
//            }
//        } catch(Exception ex) {
//            ex.printStackTrace();
//        }

        Toast.makeText(getApplicationContext(), "위치 확인이 시작되었습니다. 로그를 확인하세요.", Toast.LENGTH_SHORT).show();

    }


    public void setXY(double x, double y){
        this.X = x; this.Y = y;
        this.buildList = dbHelper.searchPos(x, y, 0.001);
//        for (int i = 0; i < buildList.size(); i++){
////            System.out.println(this.buildList.get(i));
//            buildList.get(i).printAll();
//        }
        markerView.setMarkerList(buildList, x, y);
    }

    /**
     * 리스너 클래스 정의
     */
    private class GPSListener implements LocationListener {
        /**
         * 위치 정보가 확인될 때 자동 호출되는 메소드
         */
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            String provider = location.getProvider();

            String msg = "Latitude : "+ latitude + "\nLongitude:"+ longitude;
//            Log.i("GPSListener", msg);
            GPStext.setText("\n\n\n\n" + "Latitude : " + latitude + " Longitude:" + longitude + " Provider: " + provider);
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            setXY(latitude, longitude);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}

