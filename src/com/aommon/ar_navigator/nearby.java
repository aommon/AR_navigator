package com.aommon.ar_navigator;

//This Class calculate 4 points around the Present's Point.
import java.util.Arrays;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PointF;
import android.util.Log;

public class nearby {
	public static PointF calculateDerivedPosition(PointF point,double range, double bearing){
        double EarthRadius = 6371000; // m

        double latA = Math.toRadians(point.x);
        double lonA = Math.toRadians(point.y);
        double angularDistance = range / EarthRadius;
        double trueCourse = Math.toRadians(bearing);

        double lat = Math.asin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance)
                        * Math.cos(trueCourse));

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance)
                        * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

        double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);

        PointF newPoint = new PointF((float) lat, (float) lon);

        return newPoint;
    }
	
	
	public static PointF[] nearbyLaLong (double s_la, double s_long,int distance){
		//nearby
        PointF center = new PointF((float)s_la, (float)s_long);
        final double mult = 1.1; // mult = 1.1; is more reliable
        
        PointF p1 = nearby.calculateDerivedPosition(center, mult * distance, 0); //right
        PointF p2 = nearby.calculateDerivedPosition(center, mult * distance, 90); //forward
        PointF p3 = nearby.calculateDerivedPosition(center, mult * distance, 180);//left
        PointF p4 = nearby.calculateDerivedPosition(center, mult * distance, 270);//back
        
        PointF p[] = new PointF[4];
        p[0]=p1;
        p[1]=p2;
        p[2]=p3;
        p[3]=p4;              
        return p;
	}
	
	//True degree when watching AR
	public static float true_compass(float deg){ 
		float t_de = deg+90;
        if(t_de>360){
        	t_de = t_de-360;
        }else if(t_de<0){
        	t_de = 360+t_de;
        }
        return t_de;
	}

}
