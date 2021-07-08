package org.thshsh.crypt.web.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.thshsh.crypt.User;
import org.thshsh.crypt.UserRepository;
import org.thshsh.vaadin.entity.EntityForm;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.validator.EmailValidator;

public class UserForm extends EntityForm<User,Long> {

	@Autowired
	UserRepository userRepo;

	public UserForm(User entity) {
		super(User.class, entity);
	}

	@Override
	protected JpaRepository<User, Long> getRepository() {
		return userRepo;
	}

	@Override
	protected void setupForm() {

		TextField name = new TextField("Name");
		binder
		.forField(name)
		.withNullRepresentation("")
		.bind(User::getDisplayName, User::setDisplayName);
		
		TextField emailField = new TextField("Name");
		binder
		.forField(emailField)
		.asRequired()
		.withValidator(new EmailValidator("Invalid Email Address"))
		.withValidator((s,c) -> {
			if(userRepo.findByEmail(s).isPresent()) return ValidationResult.error("Email Address already in use");
			else return ValidationResult.ok();
		})
		.bind(User::getEmail, User::setEmail);
		
		

	}

	@Override
	protected Long getEntityId(User e) {
		return e.getId();
	}

}
