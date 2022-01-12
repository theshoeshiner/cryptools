package org.thshsh.crypt;

import javax.persistence.Converter;
import javax.persistence.EnumAttributeConverter;

public enum ActivityType {
	
	Login,Register,Confirm;
	
	@Converter(autoApply = true)
	public static class ActivityConverter extends EnumAttributeConverter<ActivityType> {

		public ActivityConverter() {
			super(ActivityType.class);
		}

	}

}
