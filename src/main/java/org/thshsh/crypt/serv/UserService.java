package org.thshsh.crypt.serv;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileTypeMap;
import javax.activation.URLDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thshsh.crypt.Activity;
import org.thshsh.crypt.ActivityType;
import org.thshsh.crypt.Role;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.ActivityRepository;
import org.thshsh.crypt.repo.RoleRepository;
import org.thshsh.crypt.repo.UserRepository;

@Service
public class UserService {

	public static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	UserRepository userRepo;

	//@Autowired
	//JavaMailSender mailSender;
	
	@Autowired
	MailService mailService;

	@Autowired
	RoleRepository roleRepo;
	
	@Autowired
	ActivityRepository actRepo;

	@Value("${app.url}")
	String baseUrl;

	// public static final String CONFIRMATION_TEXT = "<html>Your Cryptools account
	// has been created. Click here to confirm your email: <a
	// href='%2$s'>%1$s<a/><html>";

	public Boolean registerUser(User u) throws UserExistsException {

		LOGGER.info("registerUser: {}", u);
		u.setEmail(u.getEmail().toLowerCase());

		boolean email = userRepo.findByEmail(u.getEmail()).isPresent();
		boolean name = u.getUserName() != null && userRepo.findByUserNameIgnoreCase(u.getUserName()).isPresent();

		if (email || name)
			throw new UserExistsException(name, email);

		u.setPassword(encoder.encode(u.getPassword()));
		u.setConfirmed(false);
		String confirmToken = RandomStringUtils.randomAlphanumeric(32);
		u.setConfirmToken(confirmToken);
		u.getRoles().add(roleRepo.findByKey(Role.USER_ROLE_KEY).get());

		
		mailService.sendAccountConfirmEmail(u, confirmToken);

		userRepo.save(u);
		
		actRepo.save(new Activity(u, ActivityType.Register));
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

	public static class StreamDataSource implements DataSource {

		public static final Logger LOGGER = LoggerFactory.getLogger(UserService.StreamDataSource.class);

		InputStream stream;
		String name;
		String contentType;

		public StreamDataSource(InputStream stream, String name) {
			super();
			LOGGER.info("name: {}", name);
			this.stream = stream;
			this.name = name;
			this.contentType = FileTypeMap.getDefaultFileTypeMap().getContentType(name);
			LOGGER.info("contentType: {}", contentType);

		}

		@Override
		public InputStream getInputStream() throws IOException {
			return stream;
		}

		@Override
		public OutputStream getOutputStream() throws IOException {
			throw new IllegalStateException();
		}

		@Override
		public String getContentType() {
			return contentType;
		}

		@Override
		public String getName() {
			return name;
		}

	}

	public static class ClasspathDataSource implements DataSource {

		public static final Logger LOGGER = LoggerFactory.getLogger(UserService.StreamDataSource.class);

		Class<?> classs;
		// InputStream stream;
		String name;
		String contentType;

		public ClasspathDataSource(Class<?> classs, String name) {
			super();

			this.classs = classs;
			this.name = name;
			this.contentType = FileTypeMap.getDefaultFileTypeMap().getContentType(name);
			LOGGER.info("name: {}", name);
			LOGGER.info("contentType: {}", contentType);

		}

		@Override
		public InputStream getInputStream() throws IOException {
			return classs.getResourceAsStream(name);
		}

		@Override
		public OutputStream getOutputStream() throws IOException {
			throw new IllegalStateException();
		}

		@Override
		public String getContentType() {
			return contentType;
		}

		@Override
		public String getName() {
			return name;
		}

	}
}
