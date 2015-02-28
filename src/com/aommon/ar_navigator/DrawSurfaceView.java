package com.aommon.ar_navigator;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class DrawSurfaceView extends View {

	Point me = new Point(-33.870932d, 151.204727d, "Me");
	Paint mPaint = new Paint();
	double OFFSET = 0d; //we aren't using this yet, that will come in the next step
	double screenWidth, screenHeight = 0d;
	Bitmap[] mSpots, mBlips;
	Context context2;
	Boolean ch_first;
	
	static ArrayList<Point> props = new ArrayList<Point>();
/*	static {
		props.add(new Point(90d, 110.8000, "North Pole"));
		props.add(new Point(-90d, -110.8000, "South Pole"));
		props.add(new Point(-33.870932d, 151.8000, "East"));
		props.add(new Point(-33.870932d, 150.8000, "West"));
		props.add(new Point(13.729735, 100.77589, "โรงอาหาร"));
		props.add(new Point(13.7296216, 100.7750383, "หอพัก"));
	}
*/
	public void getnear_lacation(ArrayList<Point> prop){
		props = prop;
		Log.e("props-size", String.format("%d", props.size()));
		ch_first=true;
	}
	
	public DrawSurfaceView(Context c, Paint paint) {
		super(c);
	}
	
	public DrawSurfaceView(Context context, AttributeSet set) {
		super(context, set);
		//Log.e("invalidate", "start draw");
		mPaint.setColor(Color.GREEN);
		mPaint.setTextSize(30);
		mPaint.setStrokeWidth(DpiUtils.getPxFromDpi(getContext(), 2));
		mPaint.setAntiAlias(true);
		
		mSpots = new Bitmap[17];
		Log.e("mSpots-size", String.format("%d", mSpots.length));
		for (int i = 0; i < mSpots.length; i++) {
			mSpots[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.dot);
			
		}
		ch_first=false;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		//Log.e("onSizeChanged", "in here w=" + w + " h=" + h);
		screenWidth = (double) w;
		screenHeight = (double) h;

	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		if(ch_first){
			//Log.e("invalidate", "on draw");
			
			for (int i = 0; i < mSpots.length; i++) {
			    Bitmap spot = mSpots[i];
			    Point u = props.get(i);

			    double distance = Harversine.haversine(me.latitude, me.longitude, u.latitude, u.longitude);
			    Log.e("name", u.description);
			    Log.e("distance", String.format("%.8f", distance));
			    if(distance<50){
				    if (spot == null)
				        continue;

				    double angle = bearing(me.latitude, me.longitude, u.latitude, u.longitude) - OFFSET;
				    double xPos, yPos;

				    if(angle < 0)
				        angle = (angle+360)%360;

				    double posInPx = angle * (screenWidth / 90d);

				    int spotCentreX = spot.getWidth() / 2;
				    int spotCentreY = spot.getHeight() / 2;
				    xPos = posInPx - spotCentreX;

				    if (angle <= 45) 
				        u.x = (float) ((screenWidth / 2) + xPos);

				    else if (angle >= 315) 
				        u.x = (float) ((screenWidth / 2) - ((screenWidth*4) - xPos));

				    else
				        u.x = (float) (float)(screenWidth*9); //somewhere off the screen

				    u.y = (float)screenHeight/2 + spotCentreY;
				    canvas.drawBitmap(spot, u.x, u.y, mPaint); //camera spot
				    canvas.drawText(u.description, u.x, u.y, mPaint); //text
			    }
			}
		}
		
	}
	
	public void setOffset(float offset) {
		//Log.e("invalidate", "setoffset");
		this.OFFSET = offset;
	}

	public void setMyLocation(double latitude, double longitude) {
		me.latitude = latitude;
		me.longitude = longitude;
	}
	
	protected static double bearing(double lat1, double lon1, double lat2, double lon2) {
		double longDiff = Math.toRadians(lon2 - lon1);
		double la1 = Math.toRadians(lat1);
		double la2 = Math.toRadians(lat2);
		double y = Math.sin(longDiff) * Math.cos(la2);
		double x = Math.cos(la1) * Math.sin(la2) - Math.sin(la1) * Math.cos(la2) * Math.cos(longDiff);

		double result = Math.toDegrees(Math.atan2(y, x));
		return (result+360.0d)%360.0d;
	}
	
}