package com.stan.person.utility;

import java.text.SimpleDateFormat;

public class Date {
    public static String dateAsString(java.util.Date date) {
    	SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
    	return sdf.format(date);
    }


}
