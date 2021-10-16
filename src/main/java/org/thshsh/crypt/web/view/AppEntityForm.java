package org.thshsh.crypt.web.view;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.thshsh.vaadin.entity.EntityForm;

@SuppressWarnings("serial")
public abstract class AppEntityForm<T,ID extends Serializable> extends EntityForm<T,ID> {

	public AppEntityForm(Class<T> eClass, ID id, Boolean loadFromId) {
		super(eClass, id, loadFromId);
	}

	public AppEntityForm(Class<T> eClass, T entity, Boolean load) {
		super(eClass, entity, load);
	}

	public AppEntityForm(Class<T> eClass, T entity) {
		super(eClass, entity);
	}
	
	@PostConstruct
	public void postConstruct() {
		this.cancelText = "Back";
		super.postConstruct();
	}
	
}
