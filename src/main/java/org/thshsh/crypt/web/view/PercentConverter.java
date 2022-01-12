package org.thshsh.crypt.web.view;

import java.math.BigDecimal;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

@SuppressWarnings("serial")
public class PercentConverter implements  Converter<BigDecimal, BigDecimal> {
	
	public static final BigDecimal HUNDRED = new BigDecimal(100l); 

	@Override
	public Result<BigDecimal> convertToModel(BigDecimal v, ValueContext context) {
		if(v == null) return Result.ok(null);
		else return Result.ok(v.divide(HUNDRED));
	}

	@Override
	public BigDecimal convertToPresentation(BigDecimal v, ValueContext context) {
		if(v == null) return null;
		else return v.multiply(HUNDRED);
	}

}
