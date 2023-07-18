package org.thshsh.crypt.web.view.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.UserRepository;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.validator.RegexpValidator;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class UserNameValidator extends RegexpValidator {
	
	@Autowired
	UserRepository userRepo;
	
	User current;

	public UserNameValidator(User current) {
		super("Invalid Username", "\\p{Alnum}++");
		this.current = current;
	}
	
	public UserNameValidator() {
		super("Invalid Username", "\\p{Alnum}++");
	}

	@Override
	public ValidationResult apply(String value, ValueContext context) {
		ValidationResult vr = super.apply(value, context);
		if(vr.isError()) return vr;
		else {
			if(value != null) {
				Optional<User> opt = userRepo.findByUserNameIgnoreCase(value);
				if(opt.isPresent() &&  (current == null || !opt.get().getId().equals(current.getId()))) {
					return ValidationResult.error("Username already in use");
				}
			}
			//if (value != null && userRepo.findByUserNameIgnoreCase(value).isPresent())
				//return ValidationResult.error("Username already in use");
		
			return ValidationResult.ok();
		}
	}

}
