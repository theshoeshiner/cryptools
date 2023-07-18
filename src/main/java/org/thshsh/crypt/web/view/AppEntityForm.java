package org.thshsh.crypt.web.view;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.thshsh.vaadin.entity.EntityForm;

@SuppressWarnings("serial")
public abstract class AppEntityForm<T,ID extends Serializable> extends EntityForm<T,ID> {

	public AppEntityForm(ID id, Boolean loadFromId) {
		super(id, loadFromId);
	}

	public AppEntityForm(T entity, Boolean load) {
		super(entity, load);
	}

	public AppEntityForm(T entity) {
		super(entity);
	}
	
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		this.getButtons().getCancel().setText("Back");
	}
	
}
