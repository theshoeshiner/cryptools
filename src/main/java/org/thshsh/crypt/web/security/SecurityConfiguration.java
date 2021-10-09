package org.thshsh.crypt.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.thshsh.crypt.UserActivity;
import org.thshsh.crypt.repo.UserRepository;
import org.thshsh.crypt.web.AppConfiguration;

/**
 * Configures spring security, doing the following:
 * <li>Bypass security checks for static resources,</li>
 * <li>Restrict access to the application, allowing only logged in users,</li>
 * <li>Set up the login form</li>

 */
@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	public static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfiguration.class);
	
	public static final Logger LOGGER_ACTIVITY = LoggerFactory.getLogger(UserActivity.class);

	private static final String LOGIN_PROCESSING_URL = "/login";
	private static final String LOGIN_FAILURE_URL = "/login?error";
	private static final String LOGIN_URL = "/login";
	private static final String LOGOUT_SUCCESS_URL = "/dashboard";
	
	public static final String ERROR_CREDENTIALS = "Username and Password do not match";
	public static final String ERROR_USER = "Username not found";
	
	public static BidiMap<String,String> ERROR_MESSAGES = new DualHashBidiMap<>();
	static {
		//BidiMap<String, String> 
		
		ERROR_MESSAGES.put(ERROR_USER, Integer.toHexString(ERROR_USER.hashCode()));
		ERROR_MESSAGES.put(ERROR_CREDENTIALS, Integer.toHexString(ERROR_CREDENTIALS.hashCode()));
		//Hex.encodeHexString(((Integer)ERROR_USER.hashCode()).byteValue());
		 
	}

	@Autowired
	UserRepository userRepo;

	@Autowired
	AppConfiguration appConfig;
	
	@Autowired
	CryptUserDetailsService userDetailsService;

	public static String PASSWORD_PARAM = "password";
	
	/**
	 * Require login to access internal pages and configure login form.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		LOGGER.info("appConfig.loginEnabled: {}",appConfig.getLoginEnabled());


		if(appConfig.getLoginEnabled()) {

			// Not using Spring CSRF here to be able to use plain HTML for the login page
			http.csrf().disable()

				// Register our CustomRequestCache, that saves unauthorized access attempts, so
				// the user is redirected after login.
				.requestCache().requestCache(new CustomRequestCache())
				// Restrict access to our application.
				.and()
				.authorizeRequests()
					.antMatchers("/login").permitAll()
					//.anyRequest().authenticated() 

					// Allow all flow internal requests.
					.requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()

				// Allow all requests by logged in users.
					.anyRequest().authenticated()

				// Configure the login page.
				.and()
				.formLogin()
					.loginPage(LOGIN_URL).permitAll()
					.passwordParameter(PASSWORD_PARAM)
					.loginProcessingUrl(LOGIN_PROCESSING_URL)
					.failureUrl(LOGIN_FAILURE_URL)
					.failureHandler((req,res,exc) -> {
					LOGGER.error("login failure",exc);
						if(exc instanceof BadCredentialsException) {
							res.sendRedirect(res.encodeRedirectURL("/login?error="+ERROR_MESSAGES.get(ERROR_CREDENTIALS)));
						}
						else {
							res.sendRedirect(res.encodeRedirectURL("/login?error="+ERROR_MESSAGES.get(ERROR_USER)));
						}

					})
					//.defaultSuccessUrl("/dashboard",false)
					.successHandler(new SavedRequestAwareAuthenticationSuccessHandler() {
					    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
					    	LOGGER.info("onAuthenticationSuccess");
					    	
					        // run custom logics upon successful login
					        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
					        String username = userDetails.getUsername();
					         LOGGER_ACTIVITY.info("User: {} logged in",username);
					        super.onAuthenticationSuccess(request, response, authentication);
					    }                      
					})

				// Configure logout
				.and()

				.logout()
				.logoutSuccessUrl(LOGOUT_SUCCESS_URL);

		}
		else {
			http.authorizeRequests().anyRequest().authenticated();
		}
	}

	@Bean
	public PasswordEncoder encoder() {
		  return new BCryptPasswordEncoder();
	   // return NoOpPasswordEncoder.getInstance();
		//return new StandardPasswordEncoder();
	}



	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		return userDetailsService;
	}

	@Bean
	public DaoAuthenticationProvider authProvider() {
	    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	    authProvider.setHideUserNotFoundExceptions(false);
	    authProvider.setUserDetailsService(userDetailsService());
	    authProvider.setPasswordEncoder(encoder());
	    return authProvider;
	}

	/**
	 * Allows access to static resources, bypassing Spring security.
	 */
	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers(
				// Vaadin Flow static resources
				"/VAADIN/**",

				// the standard favicon URI
				"/favicon.ico",
				
				//FIXME REMOVE 
				"/lv","/login2","/form", 				
				

				// the robots exclusion standard
				"/robots.txt",

				// web application manifest
				"/manifest.webmanifest",
				"/sw.js",
				"/offline-page.html",
				"/offline.html",

				// icons and images
				"/icons/**",
				"/images/**",

				// (development mode) static resources
				"/frontend/**",

				// (development mode) webjars
				"/webjars/**",

				// (development mode) H2 debugging console
				"/h2-console/**",

				// (production mode) static resources
				"/frontend-es5/**", "/frontend-es6/**");

		if(!appConfig.getLoginEnabled()) {
			web.ignoring().antMatchers("/**");
		}
	}


}