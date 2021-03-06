package com.aommon.ar_navigator;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

import com.aommon.ar_navigator.GMapV2Direction.OnDirectionResponseListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.internal.ln;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import android.R.bool;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SurfaceHolder.Callback, SensorEventListener, AutoFocusCallback {

	Camera mCamera;
    SurfaceView mPreview;
    
    SensorManager sensorManager;
    Sensor accelerometer,magnetometer;
    float degree,con_degree,azimuthInDegress,old_rotate;
    float[] mGravity;
    float[] mGeomagnetic;
    
    TextView txtHeading,textInf,txtSoLat,txtSoLng,txtBetlat,txtBetlng,txtDesLat,txtDesLng,txtAngle,txtI,txtEnd,txtFin;
    ImageView imgArr;
    Button button1;
    private float currentDegree = 0f;
	double dorm_la = 13.7294916;
	double dorm_lng = 100.77649;
    
    //Map
    LocationClient mLocationClient;
    double lat,lng,angle;
    public static final String TAG = "InMain";
    GMapV2Direction md;
    ArrayList<LatLng> arr_pos;
    int i = 0;
    boolean getInput;
    PointF a[] = new PointF[4];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN 
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
		
		mPreview = (SurfaceView)findViewById(R.id.preview);
        mPreview.getHolder().addCallback(this);
        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        
        txtHeading = (TextView) findViewById(R.id.txtHeading);
        textInf = (TextView) findViewById(R.id.textInf);
        txtSoLat = (TextView) findViewById(R.id.txtSoLat);
        txtSoLng = (TextView) findViewById(R.id.txtSoLng);
        txtBetlat = (TextView) findViewById(R.id.txtBetLat);
        txtBetlng = (TextView) findViewById(R.id.txtBetLng);
        txtDesLat = (TextView) findViewById(R.id.txtDesLat);
        txtDesLng = (TextView) findViewById(R.id.txtDesLng);
        txtAngle = (TextView) findViewById(R.id.txtAngle);
        txtI = (TextView) findViewById(R.id.txtI);
        txtEnd = (TextView) findViewById(R.id.txtEnd);
        txtFin = (TextView) findViewById(R.id.txtFin);
        imgArr = (ImageView)findViewById(R.id.imgArr);
        button1 = (Button) findViewById(R.id.button1);
        md = new GMapV2Direction(this);
        
        
        
        
        //GPS
        boolean result = isServicesAvailable();        
        if(result) {
            // �����ͧ�� Google Play Services
        	mLocationClient = new LocationClient(this, mCallback, mListener);
        } else {
            // �����ͧ����� Google Play Services �Դ�������
        	finish();
        }  
        
	}
	
	public void workspace(){
		button1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
		        LatLng startPosition = new LatLng(lat, lng);
				LatLng endPosition = new LatLng(dorm_la,dorm_lng);
				md.request(startPosition, endPosition, GMapV2Direction.MODE_DRIVING);
				
				Log.e("onclick","1");
				md.setOnDirectionResponseListener(new OnDirectionResponseListener() {
					
					
					public void onResponse(String status, Document doc, GMapV2Direction gd) {
						// TODO Auto-generated method stub
						int distance = gd.getTotalDistanceValue(doc);
						Log.e(TAG,"Total Distance : "+distance);
						int duration = gd.getTotalDurationValue(doc);
						//Log.e(TAG,"Total Duration : "+duration);
						textInf.setText("Distance : " + distance + " m\n"
								+"Duration : " + duration + " sec");
						arr_pos = gd.getDirection(doc);
						Log.e("onclick","2");
						
				     // create a rotation animation (reverse turn degree degrees)
						
						getInput = true;
					}

				});
			}
		});

		txtHeading.setText("Heading"+ (String.format("%.8f", azimuthInDegress) + "degrees"));
    	txtSoLat.setText("S_la : " + (String.format("%.8f", lat)));
		txtSoLng.setText("S_long : " + (String.format("%.8f", lng)));
		txtDesLat.setText("D_la : " + (String.format("%.8f", dorm_la)));
		txtDesLng.setText("D_long : " + (String.format("%.8f", dorm_lng)));
		
		//Log.e(TAG,"start : "+startPosition);
		//Log.e(TAG,"end : "+endPosition);
		
		//Log.e(TAG, GMapV2Direction.MODE_DRIVING);
		
		//try{
		if((azimuthInDegress > con_degree+5 || azimuthInDegress < con_degree-5) && getInput){
			con_degree = azimuthInDegress;
			
			Log.e("onclick","3");
/*			for(int j = 0 ; j < arr_pos.size() ; j++) {
                Log.e("Position " + j, arr_pos.get(j).latitude
                        + ", " + arr_pos.get(j).longitude);
			}
*/			

				double be_lat = arr_pos.get(i).latitude;
	        	double be_lng = arr_pos.get(i).longitude;
	        	Log.e("be_lat",""+(String.format("%.8f", be_lat)));
	        	Log.e("be_lng",""+(String.format("%.8f", be_lng)));
	        	txtBetlat.setText("be_lat "+ (String.format("%.8f", be_lat)));
	        	txtBetlng.setText("be_lng "+ (String.format("%.8f", be_lng)));
	        	txtI.setText("I_count "+ (String.format("%d", i)));
	        	txtEnd.setText("End "+ (String.format("%d", arr_pos.size())));
	        	
	        	a = nearby.nearbyLaLong(be_lat, be_lng);
				if(lat > a[2].x && lat < a[0].x && lng < a[1].y && lng > a[3].y ){
					Log.e("i+new",""+i);
					i++;
	        	}else if(i==arr_pos.size()) {
	        		txtFin.setText("DONE");
	        	}else{
					//Log.e("i_old",""+i);
					angle = Azimuth.initial(lat, lng, dorm_la, dorm_lng);
			        txtAngle.setText("angle : " + (String.format("%.8f", angle)));
			        
			        double true_degree = nearby.true_compass(con_degree);
			        float rotate = (float) (angle-true_degree);
			        if(rotate > 180){
			        	rotate = -(360-rotate);
			        }

			        //txtBetLat.setText("angle "+ (String.format("%.8f", rotate) + " degrees"));
			        
			        RotateAnimation ra = new RotateAnimation(
			        		old_rotate,
			        		rotate,
			                Animation.RELATIVE_TO_SELF, 0.5f, 
			                Animation.RELATIVE_TO_SELF, 0.5f
			                );
			        
			 
			        // how long the animation will take place
			        ra.setDuration(200);
			 
			        // set the animation after the end of the reservation status
			        ra.setFillAfter(true);
			 
			        // Start the animation
			        imgArr.startAnimation(ra);
			        old_rotate = rotate;

	        	}
	


		}
		
	}
	
	 public void onResume() {
        Log.d("System","onResume");
        super.onResume();
        mCamera = Camera.open();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI); 
    }
    
    public void onPause() {
        Log.d("System","onPause");
        super.onPause();
        mCamera.release();
        sensorManager.unregisterListener(this);
    }
    
    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);

    }
    protected void onStart(){
    	super.onStart();
    	//GPS
    	mLocationClient.connect();

    }

    public void surfaceChanged(SurfaceHolder arg0
            , int arg1, int arg2, int arg3) {
        Log.d("CameraSystem","surfaceChanged");
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> previewSize = params.getSupportedPreviewSizes();
        List<Camera.Size> pictureSize = params.getSupportedPictureSizes();
        int preview_index = ImageMaxSize.maxSize(previewSize);
        int picture_index = ImageMaxSize.maxSize(pictureSize);
        params.setPictureSize(previewSize.get(preview_index).width, previewSize.get(preview_index).height);
        params.setPreviewSize(previewSize.get(picture_index).width,previewSize.get(picture_index).height);
        params.setJpegQuality(100);
        mCamera.setParameters(params);
        
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void surfaceCreated(SurfaceHolder arg0) {
        Log.d("CameraSystem","surfaceCreated");
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder arg0) { }
    
    public void onAutoFocus(boolean success, Camera camera) {
        Log.d("CameraSystem","onAutoFocus");
    }
    
    @Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
    
    @Override
	public void onSensorChanged(SensorEvent event) {
		//compass
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            mGravity = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
            	float orientation[] = new float[3];
            	SensorManager.getOrientation(R, orientation);
            	degree = orientation[0]; // orientation contains: azimut, pitch and roll
            	//Log.e("azi", "" + degree);
            	azimuthInDegress = (float)Math.toDegrees(degree);
            	if (azimuthInDegress < 0.0f) {
            		azimuthInDegress += 360.0f;
            	}
            	
            }
        }
        workspace();
	}
    
  //GPS
    private ConnectionCallbacks mCallback = new ConnectionCallbacks() {
        public void onConnected(Bundle bundle) {
            // �������͡Ѻ Google Play Services ��
        	Toast.makeText(MainActivity.this, "Services connected", Toast.LENGTH_SHORT).show();

            LocationRequest mRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000).setFastestInterval(1000);

            mLocationClient.requestLocationUpdates(mRequest, locationListener);
        }

        public void onDisconnected() {
            // ��ش�������͡Ѻ Google Play Services
        	Toast.makeText(MainActivity.this, "Services disconnected", Toast.LENGTH_SHORT).show();
        }
    };
    
    private OnConnectionFailedListener mListener = new OnConnectionFailedListener() {
        public void onConnectionFailed(ConnectionResult result) {
            // ������Դ�ѭ���������͡Ѻ Google Play Services �����
        	Toast.makeText(MainActivity.this, "Services connection failed", Toast.LENGTH_SHORT).show();
        }
    };
    private boolean isServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        return (resultCode == ConnectionResult.SUCCESS);
    }

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

        	LatLng coordinate = new LatLng(location.getLatitude(),location.getLongitude());
        	lat = location.getLatitude();
        	lng = location.getLongitude();
        	workspace();
         
		}       
    };
	    
}
