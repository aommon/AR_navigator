package com.aommon.ar_navigator;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

import com.aommon.ar_navigator.Database.DatabaseHelper;
import com.aommon.ar_navigator.GMapV2Direction.OnDirectionResponseListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.internal.ln;
import com.google.android.gms.internal.ms;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import android.R.bool;
import android.R.string;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity<CustomView> extends Activity implements SurfaceHolder.Callback, SensorEventListener, AutoFocusCallback {

	String dMode;
	static String dName="";
	
	Camera mCamera;
    SurfaceView mPreview;
    
    SensorManager sensorManager;
    Sensor accelerometer,magnetometer;
    float degree,con_degree,azimuthInDegress,old_rotate;
    float x1,x2;
    float[] mGravity;
    float[] mGeomagnetic;
    
    TextView textWarn,txtCheck;
    ImageView imgArr,img_target;
    Button btnSearch,btn_showpictarget;
    ImageButton btn_imageType;
    private float currentDegree = 0f;
    float x;
    
    //Map
    LocationClient mLocationClient;
    double lat,lng,angle,dlat,dlng;
    public static final String TAG = "InMain";
    GMapV2Direction md;
    ArrayList<LatLng> arr_pos;
    int i,c=0;
    boolean getInput,click,send_data_first,isInternetPresent;
    check_internet cd;
    PointF a[] = new PointF[4];
    PointF at_target[] = new PointF[4];
    PointF near_target[] = new PointF[4];
    CustomView mCustomview;
    
    //canvas
    DrawSurfaceView mDrawView;
    public static ArrayList<Point> props = new ArrayList<Point>();
    float true_deg;
    
    //Database
    SQLiteDatabase mDb;  
    Database mHelper;  
    Cursor mCursor_near;
	
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
        
        // click autofocus
        mPreview.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mCamera.autoFocus(MainActivity.this);
            }
        });
        
        mHelper = new Database(this);
    	mDb = mHelper.getmDbHelper().getWritableDatabase();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        
        txtCheck = (TextView) findViewById(R.id.txtCheck);
        textWarn = (TextView) findViewById(R.id.textWarn);
        img_target=(ImageView) findViewById(R.id.img_target);
        imgArr = (ImageView)findViewById(R.id.imgArr);
        btnSearch = (Button) findViewById(R.id.b_search);
        btn_showpictarget =(Button) findViewById(R.id.btn_showpictarget);
        btn_imageType = (ImageButton)  findViewById(R.id.btn_imageType);
        mDrawView = (DrawSurfaceView) findViewById(R.id.view);
        
        md = new GMapV2Direction(this);
        
        boolean result = isServicesAvailable();        
        if(result) {
        	mLocationClient = new LocationClient(this, mCallback, mListener);
        } else {
        	finish();
        }  
        
        //Check Your Internet Connection For Search Location
        cd = new check_internet(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
		if(isInternetPresent){
			btnSearch.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(),
							SearchLocation.class);
					startActivityForResult(intent, 999);
				}
			});
		} else {
			Toast.makeText(getApplicationContext(), "Please connect your INTERNET!!", Toast.LENGTH_LONG).show();
		}
		send_data_first=true; //bool for send data first
	}
	
	// Receive Data From Google Maps
	protected void onActivityResult ( int requestCode, int resultCode, Intent data )
	{
		if(requestCode == 999)
		{			
			if(resultCode == RESULT_OK ){
				dName = data.getStringExtra("mydName");
				dlat = data.getDoubleExtra("mydLat", lat);
				dlng = data.getDoubleExtra("mydLong", lng);
				dMode = data.getStringExtra("mydMode");
				btnSearch.setText(dName);
				LatLng startPosition = new LatLng(lat, lng);
				LatLng endPosition = new LatLng(dlat , dlng);
				md.request(startPosition
		                , endPosition, dMode);
				md.setOnDirectionResponseListener(new OnDirectionResponseListener() {
			        public void onResponse(String status, Document doc, GMapV2Direction gd) {
		                arr_pos = gd.getDirection(doc);
		    			for(int j = 0 ; j < arr_pos.size() ; j++) {
		                    Log.e("Position " + j, arr_pos.get(j).latitude
		                            + ", " + arr_pos.get(j).longitude);
		    			}
		    			getInput = true;
		    			i = 0;
		    			imgArr.setImageResource(R.drawable.arrow_green);	
			        }
				});
				click =true;
				btn_imageType.setVisibility(View.VISIBLE);
				if(dMode .equals("driving")){
					btn_imageType.setImageResource(R.drawable.icon_driving);
				}else{
					btn_imageType.setImageResource(R.drawable.icon_walking2);
				}
				btn_showpictarget.setVisibility(View.VISIBLE);
			}
		}
	}

	public void workspace(){
		
		if(click){
			//Set pic of image type and requested new data route from Google Maps
			btn_imageType.setOnClickListener(new OnClickListener() {			
				public void onClick(View v) {
					if(c%2 == 1){
						btn_imageType.setImageResource(R.drawable.icon_driving);
						//Google
						LatLng startPosition = new LatLng(lat, lng);
						LatLng endPosition = new LatLng(dlat , dlng);
						md.request(startPosition
				                , endPosition, GMapV2Direction.MODE_DRIVING);
						md.setOnDirectionResponseListener(new OnDirectionResponseListener() {
					        public void onResponse(String status, Document doc, GMapV2Direction gd) {
				                arr_pos = gd.getDirection(doc);
				    			for(int j = 0 ; j < arr_pos.size() ; j++) {
				                    Log.e("Position " + j, arr_pos.get(j).latitude
				                            + ", " + arr_pos.get(j).longitude);
				    			}
				    			getInput = true;
				    			i = 0;
				    			imgArr.setImageResource(R.drawable.arrow_green);	
					        }
						});
					}else{
						btn_imageType.setImageResource(R.drawable.icon_walking2);
						//Google
						LatLng startPosition = new LatLng(lat, lng);
						LatLng endPosition = new LatLng(dlat , dlng);
						md.request(startPosition
				                , endPosition, GMapV2Direction.MODE_WALKING);
						Log.e("onclick","1");
			        
						md.setOnDirectionResponseListener(new OnDirectionResponseListener() {
					        public void onResponse(String status, Document doc, GMapV2Direction gd) {
				                arr_pos = gd.getDirection(doc);
				    			for(int j = 0 ; j < arr_pos.size() ; j++) {
				                    Log.e("Position " + j, arr_pos.get(j).latitude
				                            + ", " + arr_pos.get(j).longitude);
				    			}
				    			getInput = true;
				    			i = 0;
				    			imgArr.setImageResource(R.drawable.arrow_green);	
					        }
						});
					
					}					
					c++;					
				}
			});
			
			//Calculate for Show Image Target
			btn_showpictarget.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					img_target.setVisibility(View.VISIBLE);
					double pic_tar = (dlat - 13)*10000000;
					String name_img_target = "a" + String.format("%.0f", pic_tar);
					int id_img_target = Get_ID_image.getDrawable(getApplicationContext(), name_img_target);
					img_target.setImageResource(id_img_target);
				}
			});
			//Invisible Image Target
			mPreview.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	            	img_target.setVisibility(View.INVISIBLE);
	            }
	        });
		}
		
		//retrieve all data from SQLite and sent to DrawSurfaceView.java (send only one time)
		if(send_data_first){	    	
			if (lat != 0 && lng != 0){
				mCursor_near = mDb.rawQuery("SELECT  " +  Database.COL_DISPLAY_NAME  + "," + Database.COL_LATITUDE + "," + 
	        		Database.COL_LONGITUDE  + " FROM " + Database.TABLE_NAME , null);
			   	mCursor_near.moveToFirst();
		        if(mCursor_near.moveToFirst()){
		        	props.clear();
		        	do{	        	
		        		double latitude = mCursor_near.getDouble(mCursor_near.getColumnIndex(Database.COL_LATITUDE));
		        		double longitude = mCursor_near.getDouble(mCursor_near.getColumnIndex(Database.COL_LONGITUDE));
		        		String name = mCursor_near.getString(mCursor_near.getColumnIndex(Database.COL_DISPLAY_NAME));	        		
			        	props.add(new Point(latitude, longitude, name));
		        	} while (mCursor_near.moveToNext());
		        	mDrawView.getnear_lacation(props);
		        	mDrawView.invalidate();
		        }
		        send_data_first=false;
			}
		}

		//Destination checked 
		if(getInput){		
			//Check Present's Point near Destination's Point less than 10 m.
			near_target = nearby.nearbyLaLong(dlat, dlng, 10);
			if(lat > near_target[2].x && lat < near_target[0].x && lng < near_target[1].y && lng > near_target[3].y ){
				//rotate arrow to point to Destination
				double t_angle = Azimuth.initial(lat, lng, dlat, dlng);
				old_rotate = Navigator.Rotate_arrow(azimuthInDegress, t_angle, old_rotate, imgArr);
				//Check Present's Point near Destination's Point less than 7 m.
				at_target = nearby.nearbyLaLong(dlat, dlng,7);
				if(lat > at_target[2].x && lat < at_target[0].x && lng < at_target[1].y && lng > at_target[3].y ){
					getInput = false;	
					Toast.makeText(getApplicationContext(), "Reached Destination", Toast.LENGTH_LONG).show();
					imgArr.setImageBitmap(null);
					dName = "";
				}
			} else {
				
				double be_lat = arr_pos.get(i).latitude; //Latitude from Routed By Google Map
	        	double be_lng = arr_pos.get(i).longitude; //Longitude from Routed By Google Map
	        	a = nearby.nearbyLaLong(be_lat, be_lng,7);
	        	//Check Present's Point near THIS Routed Google Maps's point OR NEXT Routed Google Maps's point
				if(lat > a[2].x && lat < a[0].x && lng < a[1].y && lng > a[3].y ){
					i++;
	         	}else{
	        		angle = Azimuth.initial(lat, lng, be_lat, be_lng);
	                old_rotate = Navigator.Rotate_arrow(azimuthInDegress, angle, old_rotate, imgArr);
	        	}
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
        dName = "";
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
            //Accelerometer
            x = event.values[0];
            mDrawView.getAcc((int) x);
            Log.e("acc_main", String.format("%d", (int)x));
        	//compass
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
            	azimuthInDegress = (float)Math.toDegrees(degree);
            	if (azimuthInDegress < 0.0f) {
            		azimuthInDegress += 360.0f;
            	}
            }
        }
        //canvas        
        x1=x2;
        x2=azimuthInDegress;
        if(x2 > x1+1 || x2 < x1-1){
        	true_deg = nearby.true_compass(x2); //true degree
        }
        //Send True Degree to DrawSurfaceView.java
    	if (mDrawView != null) {
			mDrawView.setOffset(true_deg);
			mDrawView.invalidate();
		}
        workspace();
	}
    
    
    
    
  //GPS
    private ConnectionCallbacks mCallback = new ConnectionCallbacks() {
        public void onConnected(Bundle bundle) {
            LocationRequest mRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000).setFastestInterval(1000);

            mLocationClient.requestLocationUpdates(mRequest, locationListener);
        }

        public void onDisconnected() {
        	Toast.makeText(MainActivity.this, "Services disconnected", Toast.LENGTH_SHORT).show();
        }
    };
    
    private OnConnectionFailedListener mListener = new OnConnectionFailedListener() {
        public void onConnectionFailed(ConnectionResult result) {
            
        	Toast.makeText(MainActivity.this, "Services connection failed", Toast.LENGTH_SHORT).show();
        }
    };
    private boolean isServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        return (resultCode == ConnectionResult.SUCCESS);
    }

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
        	lat = location.getLatitude();
        	lng = location.getLongitude();
        	mDrawView.setMyLocation(location.getLatitude(), location.getLongitude());
			mDrawView.invalidate();
        	workspace();         
		}       
    };  
    
	static public String send_choose_name(){
		if(dName != ""){
			return dName;
		}else
			return "xxx";
	}
}