package com.stan.person.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Math {

    public static  Double setPrecision(Double in, int digits ) {
        return BigDecimal.valueOf(in).setScale(digits, RoundingMode.HALF_UP).doubleValue();
    }
}
