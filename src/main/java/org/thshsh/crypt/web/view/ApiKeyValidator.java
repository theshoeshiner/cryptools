package org.thshsh.crypt.web.view;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.cryptocompare.CryptoCompare;
import org.thshsh.crypt.cryptocompare.CryptoCompareException;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class ApiKeyValidator implements Validator<String> {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyValidator.class);
	
	@Autowired
	CryptoCompare compare;
	
	@Override
	public ValidationResult apply(String s, ValueContext context) {
		if (s == null || s.length() == 64 && StringUtils.isAlphanumeric(s)) {
			//String oldKey = compare.getApiKey();
			try {
				compare.getApiKeyThreadLocal().set(s);
				compare.getCurrentPrice("USD", "BTC");
				return ValidationResult.ok();
			} 
			catch (CryptoCompareException e) {
				LOGGER.warn("api key validation error",e);
				return ValidationResult.error("Invalid API Key");
			}
			finally {
				//compare.setApiKey(oldKey);
			}
			
		} 
		//else {
		return ValidationResult.error("Invalid API Key");
		//}
	}
	
}