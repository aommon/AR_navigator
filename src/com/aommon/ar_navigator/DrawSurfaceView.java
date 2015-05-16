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

	Point me = new Point(-33.870932d, 151.204727d, "Me"); //assume present's point
	Paint mPaint = new Paint();
	Paint tPaint = new Paint();
	Paint mPaint2 = new Paint();
	Paint tPaint2 = new Paint();
	Paint at_mPaint = new Paint();
	Paint at_tPaint = new Paint();
	FontMetrics fm = new FontMetrics();
	double OFFSET = 0d; //we aren't using this yet, that will come in the next step
	double screenWidth, screenHeight = 0d;
	Boolean ch_first;	
	String name;
	float r1=0,l1=0,r2=0,l2=0,right,left,top,bottom;
	Bitmap spot;
	static ArrayList<Point> props = new ArrayList<Point>();
	boolean check_pic;
	int x_accel;
	
	Bitmap[] ic_coffee,ic_parking,ic_food,ic_7,ic_health,ic_atm,ic_copier;
	int c =0,a_parking=0,a_coffee=0,a_food=0,a_7=0,a_health=0,a_atm=0,a_copier=0;
	
	//แก้ไขตรงนี้นะค่ะเพิ่ม size
	//When you insert new data, you must to change this size.
	int size=204,size_parking=26,size_coffee=22,size_food=13,size_7=3,size_health=1,size_atm=8,size_copier=5; 
	

	public void getnear_lacation(ArrayList<Point> prop){
		props = prop;
		ch_first=true;
	}
	
	public DrawSurfaceView(Context c, Paint paint) {
		super(c);
	}
	
	public DrawSurfaceView(Context context, AttributeSet set) {
		super(context, set);
		//Paint of NEAR Label
		mPaint.setColor(Color.CYAN);
        mPaint.setStyle(Style.FILL);
        mPaint.setAlpha(100);
        tPaint.setColor(Color.WHITE);
        tPaint.setTextSize(40);
        
        //Paint of FAR Label 
        mPaint2.setColor(Color.CYAN);
        mPaint2.setStyle(Style.FILL);
        mPaint2.setAlpha(100);
        tPaint2.setColor(Color.WHITE);
        tPaint2.setTextSize(30);
        
        //Paint of Target
        at_mPaint.setColor(Color.GREEN);
        at_mPaint.setStyle(Style.FILL);
        at_mPaint.setAlpha(100);
        at_tPaint.setColor(Color.WHITE);
        at_tPaint.setTextSize(40);
		
        //------------ Create Icon image : Call only first time  ----------------//
        //copier
        ic_copier = new Bitmap[size_copier];
		Log.e("mSpots-size_copier", String.format("%d", ic_copier.length));
		for (int i = 0; i < ic_copier.length; i++) {
			ic_copier[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_copier);
		}
		a_copier=0;
        //coffee
        ic_coffee = new Bitmap[size_coffee];
		Log.e("mSpots-size_coffee", String.format("%d", ic_coffee.length));
		for (int i = 0; i < ic_coffee.length; i++) {
			ic_coffee[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_coffee);
		}
		a_coffee=0;
		//parking
		ic_parking = new Bitmap[size_parking];
		for (int i = 0; i < ic_parking.length; i++) {
			ic_parking[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_parking);
		}
		//food
		ic_food = new Bitmap[size_food];
		for (int i = 0; i < ic_food.length; i++) {
			ic_food[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_food);
		}
		//7-11
		ic_7 = new Bitmap[size_7];
		for (int i = 0; i < ic_7.length; i++) {
			ic_7[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_7);
		}
		//health
		ic_health = new Bitmap[size_health];
		for (int i = 0; i < ic_health.length; i++) {
			ic_health[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_health);
		}
		//atm
		ic_atm = new Bitmap[size_atm];
		for (int i = 0; i < ic_atm.length; i++) {
			ic_atm[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_atm);
		}
		
		ch_first=false;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		screenWidth = (double) w;
		screenHeight = (double) h;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if(ch_first){
			for (int i = 0; i < size; i++) {
			    Point u = props.get(i);
			    name = MainActivity.send_choose_name(); //display_name
			    double distance = Harversine.haversine(me.latitude, me.longitude, u.latitude, u.longitude);
			    
			    //Check name for make icon image
		    	if(u.description .equals("coffee shop")){
		    		spot = ic_coffee[a_coffee];
		    		a_coffee++;
		    		if(a_coffee==size_coffee)
		    			a_coffee=0;
		    		check_pic=true;
		    	}else if(u.description .equals("ลานจอดรถ")){
		    		spot = ic_parking[a_parking];
		    		a_parking++;
		    		if(a_parking==size_parking)
		    			a_parking=0;
		    		check_pic=true;
		    	}else if(u.description .equals("ร้านอาหาร")){
		    		spot = ic_food[a_food];
		    		a_food++;
		    		if(a_food==size_food)
		    			a_food=0;
		    		check_pic=true;
		    	}else if(u.description .equals("7-ELEVEN")){
		    		spot = ic_7[a_7];
		    		a_7++;
		    		if(a_7==size_7)
		    			a_7=0;
		    		check_pic=true;
		    	}else if(u.description .equals("ร้านขายยา")){
		    		spot = ic_health[a_health];
		    		a_health++;
		    		if(a_health==size_health)
		    			a_health=0;
		    		check_pic=true;
		    	}else if(u.description .equals("ATM")){
		    		spot = ic_atm[a_atm];
		    		a_atm++;
		    		if(a_atm==size_atm)
		    			a_atm=0;
		    		check_pic=true;
		    	}else if(u.description .equals("ร้านถ่ายเอกสาร")){
		    		spot = ic_copier[a_copier];
		    		a_copier++;
		    		if(a_copier==size_copier)
		    			a_copier=0;
		    		check_pic=true;
		    	}else{
		    		check_pic=false;
		    	}
			    
		    	
			    if (distance<50 && x_accel>8){ 	
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
				    
				    //set position left right
			    	if(u.description .equals(name)){				    	
			            left = (u.x-20)-at_tPaint.measureText(u.description)/2;
			            right = (u.x+20) + at_tPaint.measureText(u.description)/2;
			    	}else if(distance<25){
			            left = (u.x-20)-tPaint.measureText(u.description)/2;
			            right = (u.x+20) + tPaint.measureText(u.description)/2;
				    }else{				    	
			            left = (u.x-20)-tPaint2.measureText(u.description)/2;
			            right = (u.x+20) + tPaint2.measureText(u.description)/2;
				    }
			    	r1=r2;
				    r2=right;
				    l1=l2;
				    l2=left;

				    if (check_pic){
				    	u.y = (float)screenHeight/9 + spotCentreY + 180;
				    }else{
				    	 if(l2<r1 && r2<l1){
					    	u.y = (float)screenHeight/9 + spotCentreY + 120;
					     }else{
						    u.y = (float)screenHeight/9 + spotCentreY ; //top
					     }
				    }

			    	if(u.description .equals(name)){
				    	at_tPaint.setTextAlign(Paint.Align.CENTER);
				    	at_tPaint.getFontMetrics(fm);
					    if(!check_pic){
					    	canvas.drawRect(left, (u.y-20) - at_tPaint.getTextSize(), right, (u.y+20), at_mPaint);
					    	canvas.drawText(u.description, u.x, u.y ,at_tPaint);
					    }					    	
			            if(check_pic)
			            	canvas.drawBitmap(spot, u.x, u.y, at_tPaint);
			    	}else if(distance<25){
				    	tPaint.setTextAlign(Paint.Align.CENTER);
					    tPaint.getFontMetrics(fm);					    
					    if(!check_pic){
					    	canvas.drawRect(left, (u.y-20) - tPaint.getTextSize(), right, (u.y+20), mPaint);
					    	canvas.drawText(u.description, u.x, u.y ,tPaint);
					    }
					    if(check_pic)
					    	canvas.drawBitmap(spot, u.x, u.y, tPaint);
				    }else{
				    	tPaint2.setTextAlign(Paint.Align.CENTER);
					    tPaint2.getFontMetrics(fm);		    
					    if(!check_pic){
					    	canvas.drawRect(left, (u.y-20) - tPaint2.getTextSize(), right, (u.y+20), mPaint2);
					    	canvas.drawText(u.description, u.x, u.y ,tPaint2);
					    }					    	
					    if(check_pic)
					    	canvas.drawBitmap(spot, u.x, u.y, tPaint2);
				    }
			    	
			    }
			}
		}
	}
	
	public void getAcc(int x_acc) {
		this.x_accel = x_acc;
	}
	
	public void setOffset(float offset) {
		this.OFFSET = offset;
	}

	public void setMyLocation(double latitude, double longitude) {
		me.latitude = latitude;
		me.longitude = longitude;
	}
}