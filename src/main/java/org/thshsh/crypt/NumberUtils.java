package org.thshsh.crypt;

import java.math.BigDecimal;

public class NumberUtils {

	public static int BigDecimalToPercentInt(BigDecimal bd) {
		return (int)(bd.floatValue()*100);
	}

}
