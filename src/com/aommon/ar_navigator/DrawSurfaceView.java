package com.aommon.ar_navigator;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class DrawSurfaceView extends View {

	Point me = new Point(-33.870932d, 151.204727d, "Me");
	Paint mPaint = new Paint();
	Paint tPaint = new Paint();
	Paint mPaint2 = new Paint();
	Paint tPaint2 = new Paint();
	Paint at_mPaint = new Paint();
	Paint at_tPaint = new Paint();
	FontMetrics fm = new FontMetrics();
	double OFFSET = 0d; //we aren't using this yet, that will come in the next step
	double screenWidth, screenHeight = 0d;
	Bitmap[] mSpots, mBlips;
	Boolean ch_first;
	int c =0;
	String name;
	
	static ArrayList<Point> props = new ArrayList<Point>();

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
		mPaint.setColor(Color.DKGRAY);
        mPaint.setStyle(Style.FILL);
        mPaint.setAlpha(100);
        tPaint.setColor(Color.WHITE);
        tPaint.setTextSize(40);
        
        mPaint2.setColor(Color.DKGRAY);
        mPaint2.setStyle(Style.FILL);
        mPaint2.setAlpha(100);
        tPaint2.setColor(Color.WHITE);
        tPaint2.setTextSize(30);
        
        at_mPaint.setColor(Color.GREEN);
        at_mPaint.setStyle(Style.FILL);
        at_mPaint.setAlpha(100);
        at_tPaint.setColor(Color.WHITE);
        at_tPaint.setTextSize(40);
		
		mSpots = new Bitmap[18];
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
			Log.e("invalidate", "on draw");
			
			for (int i = 0; i < mSpots.length; i++) {
			    Bitmap spot = mSpots[i];
			    Point u = props.get(i);
			    name = MainActivity.send_choose_name();
			    Log.e("send_name", ""+name);

			    double distance = Harversine.haversine(me.latitude, me.longitude, u.latitude, u.longitude);
			    //Log.e("name", u.description);
			    //Log.e("distance", String.format("%.8f", distance));

			    c++;
			    if (spot == null)
			        continue;
			    
			    double angle = Azimuth.initial(me.latitude, me.longitude, u.latitude, u.longitude) - OFFSET;

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

			    if(c%2 == 1){
			    	u.y = (float)screenHeight/5 + spotCentreY + 120;
			    } else {
			    	u.y = (float)screenHeight/5 + spotCentreY ;
			    }
			    
			    if (distance<50){
			    	if(u.description .equals(name)){
				    	at_tPaint.setTextAlign(Paint.Align.CENTER);
				    	at_tPaint.getFontMetrics(fm);
					    u.description.length();
					    canvas.drawRect((u.x-20)-at_tPaint.measureText(u.description)/2, (u.y-20) - at_tPaint.getTextSize(), (u.x+20) + at_tPaint.measureText(u.description)/2, (u.y+20), at_mPaint);
			            canvas.drawText(u.description, u.x, u.y ,at_tPaint);
			            Log.e("name", ""+u.description);
			            Log.e("namehere", "2");
			    	}else if(distance<25){
				    	tPaint.setTextAlign(Paint.Align.CENTER);
					    tPaint.getFontMetrics(fm);
					    u.description.length();
					    canvas.drawRect((u.x-20)-tPaint.measureText(u.description)/2, (u.y-20) - tPaint.getTextSize(), (u.x+20) + tPaint.measureText(u.description)/2, (u.y+20), mPaint);
			            canvas.drawText(u.description, u.x, u.y ,tPaint);
			            Log.e("name", ""+u.description);
			            Log.e("namehere", "1");
				    }else{
				    	tPaint2.setTextAlign(Paint.Align.CENTER);
					    tPaint2.getFontMetrics(fm);
					    u.description.length();
					    canvas.drawRect((u.x-20)-tPaint2.measureText(u.description)/2, (u.y-20) - tPaint2.getTextSize(), (u.x+20) + tPaint2.measureText(u.description)/2, (u.y+20), mPaint2);
			            canvas.drawText(u.description, u.x, u.y ,tPaint2);
			            Log.e("name", ""+u.description);
			            Log.e("namehere", "3");
				    }
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
	
}