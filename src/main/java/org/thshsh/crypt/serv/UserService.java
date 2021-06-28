package org.thshsh.crypt.serv;

import javax.management.relation.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thshsh.crypt.User;
import org.thshsh.crypt.UserRepository;

@Service
public class UserService {

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	UserRepository userRepo;

	public Boolean registerUser(User u) throws UserExistsException  {

		boolean email = userRepo.findByEmail(u.getEmail().toLowerCase()).isPresent();
		boolean name = userRepo.findByUserNameIgnoreCase(u.getUserName()).isPresent();

		if(email || name) throw new UserExistsException(name, email);

		u.setPassword(encoder.encode(u.getPassword()));

	    userRepo.save(u);
	    return true;
	}

	@SuppressWarnings("serial")
	public static class UserExistsException extends Exception {

		Boolean nameExists;
		Boolean emailExists;

		public UserExistsException(Boolean username, Boolean email) {
			this.nameExists = username;
			this.emailExists = email;
		}
		public Boolean getNameExists() {
			return nameExists;
		}
		public void setNameExists(Boolean nameExists) {
			this.nameExists = nameExists;
		}
		public Boolean getEmailExists() {
			return emailExists;
		}
		public void setEmailExists(Boolean emailExists) {
			this.emailExists = emailExists;
		}


	}

}
