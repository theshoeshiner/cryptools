package org.thshsh.crypt.web.view.user;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.User;
import org.thshsh.vaadin.entity.EntityDialog;

@Component
@Scope("prototype")
public class ChangePasswordFormDialog extends EntityDialog<ChangePasswordEntity,Long> {

	public ChangePasswordFormDialog(User user) {
		super(ChangePasswordForm.class, new ChangePasswordEntity(user));
		this.setWidth("400px");
	}
	
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		this.setCloseOnOutsideClick(true);
	}
	
}