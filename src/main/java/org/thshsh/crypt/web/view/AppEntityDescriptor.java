package org.thshsh.crypt.web.view;

import org.thshsh.crypt.IdedEntity;
import org.thshsh.vaadin.entity.EntityDescriptor;

public abstract class AppEntityDescriptor<T extends IdedEntity> extends EntityDescriptor<T, Long> {

	public AppEntityDescriptor(Class<T> entityClass) {
		super(entityClass);
	}

	@Override
	public Long getEntityId(T e) {
		return e.getId();
	}

	@Override
	public Long createEntityId(String s) {
		return Long.valueOf(s);
	}

	
	
}
