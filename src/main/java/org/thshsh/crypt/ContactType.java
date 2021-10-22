package org.thshsh.crypt;

import javax.persistence.Converter;
import javax.persistence.EnumAttributeConverter;

public enum ContactType {
	
	Question,Suggestion,Issue;
	
	@Converter(autoApply = true)
	public static class ContactTypeConverter extends EnumAttributeConverter<ContactType> {

		public ContactTypeConverter() {
			super(ContactType.class);
		}

	}

}
