package com.aommon.ar_navigator;

//This Class calculate the angle of LatLng.
public class Azimuth {
	
	//This Method converse Degree to Radius 
	static Double degToRad = Math.PI / 180.0;
	
	//This Method return the angle by using bearing Method.
    static public Double initial (Double lat1, Double long1, Double lat2, Double long2)
    {
        return (bearing(lat1, long1, lat2, long2) + 360.0) % 360;
    }

    //This Method calculate the angle by using Bearing Formula.
    public static Double bearing(Double lat1, Double long1, Double lat2, Double long2)
    {
        Double phi1 = lat1 * degToRad;
        Double phi2 = lat2 * degToRad;
        Double lam1 = long1 * degToRad;
        Double lam2 = long2 * degToRad;

        return Math.atan2(Math.sin(lam2-lam1)*Math.cos(phi2),
            Math.cos(phi1)*Math.sin(phi2) - Math.sin(phi1)*Math.cos(phi2)*Math.cos(lam2-lam1)
        ) * 180/Math.PI;
    }

}
