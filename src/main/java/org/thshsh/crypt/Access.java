

package org.thshsh.crypt;

import javax.persistence.Converter;
import javax.persistence.EnumAttributeConverter;

public enum Access {
	
	Read,
	ReadWrite,
	ReadWriteDelete,
	Super // super bypasses ownership restrictions
	;

	public boolean isGreaterThanOrEqual(Access a2) {
		if(a2 == null) return true;
		return ordinal() >= a2.ordinal();
	}
	
	public boolean isLessThanOrEqual(Access a2) {
		if(a2 == null) return false;
		return ordinal()<= a2.ordinal();
	}
	

	@Converter(autoApply = true)
	public static class AccessConverter extends EnumAttributeConverter<Access> {

		public AccessConverter() {
			super(Access.class);
		}

	}
}
