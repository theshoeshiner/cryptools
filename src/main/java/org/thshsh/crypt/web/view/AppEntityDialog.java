package org.thshsh.crypt.web.view;

import org.thshsh.vaadin.entity.EntityDialog;
import org.thshsh.vaadin.entity.EntityForm;

@SuppressWarnings("serial")
public abstract class AppEntityDialog<T> extends EntityDialog<T,Long>{

	public AppEntityDialog(Class<? extends EntityForm<T, Long>> formClass, T entity) {
		super(formClass, entity);
	}



}
