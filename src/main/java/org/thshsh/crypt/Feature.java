package org.thshsh.crypt;

import javax.persistence.Converter;
import javax.persistence.EnumAttributeConverter;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Feature {
	
	Portfolio(Portfolio.class),
	User(User.class),
	Currency(Currency.class),
	Exchange(Exchange.class),
	System();
	
	public static final Logger LOGGER = LoggerFactory.getLogger(Feature.class);
	
	Class<?>[] classes;
	
	private Feature(Class<?>...classes) {
		this.classes = classes;
	}
	
	@Converter(autoApply = true)
	public static class FeatureConverter extends EnumAttributeConverter<Feature> {

		public FeatureConverter() {
			super(Feature.class);
		}

	}
	
	public static Feature getFeatureForEntity(Class<?> classs) {
		for(Feature f : Feature.values()) {
			if(ArrayUtils.contains(f.classes, classs)) {
				LOGGER.info("getFeatureForEntity {} = {}",classs,f);
				return f;
			}
		}
		return null;
		
	}
}