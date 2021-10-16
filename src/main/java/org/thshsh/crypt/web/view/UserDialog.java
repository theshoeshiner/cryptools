package org.thshsh.crypt.web.view;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.User;
import org.thshsh.vaadin.entity.EntityForm;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class UserDialog extends AppEntityDialog<User> {

	@Autowired
	ApplicationContext context;
	
	Boolean profile = false;
	
	public UserDialog(User entity) {
		super(UserForm.class,entity);
	}
	
	public UserDialog(User entity,Boolean profile) {
		super(UserForm.class, entity);
		this.profile = profile;
	}
	
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		this.setCloseOnOutsideClick(true);
		this.setWidth("400px");
	}

	@Override
	protected EntityForm<User, Long> createEntityForm() {
		return context.getBean(entityFormClass,entity,profile);
	}
	
	

}
