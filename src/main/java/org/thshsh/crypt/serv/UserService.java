package org.thshsh.crypt.serv;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.UserRepository;

@Service
public class UserService {

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	UserRepository userRepo;

	@Autowired
	JavaMailSender mailSender;
	
	@Value("${app.url}")
	String baseUrl;
	
	public static final String CONFIRMATION_TEXT = "<html>Your Cryptools account has been created. Click here to confirm your email: <a href='%2$s'>%1$s<a/><html>";
	
	public Boolean registerUser(User u) throws UserExistsException  {

		boolean email = userRepo.findByEmail(u.getEmail().toLowerCase()).isPresent();
		boolean name = userRepo.findByUserNameIgnoreCase(u.getUserName()).isPresent();

		if(email || name) throw new UserExistsException(name, email);

		u.setPassword(encoder.encode(u.getPassword()));
		u.setConfirmed(false);
		String confirmToken = RandomStringUtils.randomAlphanumeric(32); 
		u.setConfirmToken(confirmToken);
		
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
		//String htmlMsg = "<h3>Hello World!</h3>";
		
		String htmlMsg = String.format(CONFIRMATION_TEXT, confirmToken,baseUrl+"login?confirm="+confirmToken);
		
		//mimeMessage.setContent(htmlMsg, "text/html");
		try {
			helper.setText(htmlMsg, true); // Use this or above line.
			helper.setTo(u.getEmail());
			helper.setSubject("Cryptools Account Confirmation");
			helper.setFrom("cryptools@thshsh.org");
		} 
		catch (MessagingException e) {
			throw new IllegalStateException(e);
		}
		
		
		
		mailSender.send(mimeMessage);
		
		/*SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("cryptools@thshsh.org");
		message.setTo(u.getEmail());
		message.setSubject("Account Confirmation");
		message.setText(emailText.toString());*/
        
        //mailSender.send(message);

	    userRepo.save(u);
	    return true;
	}

	public void setPassword(User u, String pass) {
		u.setPassword(encoder.encode(pass));
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
