package com.aommon.ar_navigator;

import junit.framework.Assert;
import android.content.Context;

//This class get ID's Image from Image's name.
public class Get_ID_image {

	 public static int getDrawable(Context context, String name)
	    {
	        Assert.assertNotNull(context);
	        Assert.assertNotNull(name);

	        return context.getResources().getIdentifier(name,
	                "drawable", context.getPackageName());
	    }
}
