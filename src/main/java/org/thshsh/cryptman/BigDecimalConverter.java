package org.thshsh.cryptman;

import java.math.BigDecimal;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;


@SuppressWarnings({ "serial"})
public class BigDecimalConverter implements Converter<String,BigDecimal> {


	public BigDecimalConverter() {}

	@Override
	public Result<BigDecimal> convertToModel(String value, ValueContext context) {
		try {
			if(value == null) return Result.ok(null);
			value = value.replaceAll(",", "");
			return Result.ok(new BigDecimal(value));
		}
		catch (NumberFormatException e) {
			return Result.error("Invalid Format");
			//throw new IllegalArgumentException(e);
		}
	}

	@Override
	public String convertToPresentation(BigDecimal value, ValueContext context) {
		if(value==null)return null;
		return value.toString();
	}


}
