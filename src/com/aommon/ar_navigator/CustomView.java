package com.aommon.ar_navigator;

import java.util.Random;

import android.R.color;
import android.R.string;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CustomView extends View {
	double OFFSET = 0d;
	String pl_name="";
	double s_la = 0d,s_ln,d_la,d_ln;

	public CustomView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	

    public CustomView(Context context,AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
    protected void onDraw(Canvas canvas) {
		//canvas.drawColor(Color.WHITE);
        Paint p = new Paint();
/*      p.setColor(Color.GREEN);
        p.setStrokeWidth(5);
        canvas.drawLine(20, 0, 20, canvas.getHeight(), p);
        
        p.setColor(Color.BLACK);
        p.setStrokeWidth(3);
        canvas.drawRect(100, 10, 550, 550, p);
        
        p.setStrokeWidth(0);
        p.setColor(Color.CYAN);
        canvas.drawRect(33, 60, 77, 77, p );
        p.setColor(Color.YELLOW);
        canvas.drawRect(33, 33, 77, 60, p );
*/        
        p.setColor(Color.BLACK); 
        p.setTextSize(20); 
        canvas.drawText(pl_name, 10, 25, p); 
        
        p.setColor(Color.BLACK); 
        p.setTextSize(20); 
        canvas.drawText(String.format("%.1f", s_la), 100, 25, p); 

 /*       p.setColor(Color.BLACK); 
        p.setTextSize(20); 
        canvas.drawText(s_ln, 10, 25, p); 
        
        p.setColor(Color.BLACK); 
        p.setTextSize(20); 
        canvas.drawText(d_la, 10, 25, p); 
        
        p.setColor(Color.BLACK); 
        p.setTextSize(20); 
        canvas.drawText(d_ln, 10, 25, p); 
  */      
    }
	
	public void setOffset(float offset) {
		this.OFFSET = offset;
	}
	
	public void getname(String name){
		pl_name = name;
	}

	public void getPara(double s_lat,double s_lng,double d_lat, double d_lng){
		double deg = Azimuth.initial(s_lat, s_lng, d_lat, d_lng);
	}

}
