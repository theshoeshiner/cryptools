package org.thshsh.crypt.web.view.user;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.User;
import org.thshsh.crypt.web.view.AppEntityDialog;
import org.thshsh.crypt.web.view.user.UserForm.Type;
import org.thshsh.vaadin.entity.EntityForm;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class UserDialog extends AppEntityDialog<User> {

	@Autowired
	ApplicationContext context;
	
	//Boolean profile = false;
	UserForm.Type type;
	
	public UserDialog(User entity) {
		super(UserForm.class,entity);
		this.type = Type.Admin;
	}
	
	public UserDialog(User entity,UserForm.Type type) {
		super(UserForm.class, entity);
		this.type = type;
	}
	
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		this.setCloseOnOutsideClick(true);
		if(type == Type.Admin) {
			this.setWidth("800px");
		}
		else {
			this.setWidth("400px");
		}
	}

	@Override
	protected EntityForm<User, Long> createEntityForm() {
		return context.getBean(entityFormClass,entity,type);
	}
	
	

}
