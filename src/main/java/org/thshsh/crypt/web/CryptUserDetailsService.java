package org.thshsh.crypt.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.UserRepository;

@Service
public class CryptUserDetailsService implements UserDetailsService {

	public static final Logger LOGGER = LoggerFactory.getLogger(CryptUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    public CryptUserDetailsService() {}

    @Override
    public UserDetails loadUserByUsername(String username) {
    	
    	DaoAuthenticationProvider dap;
    	
    	LOGGER.info("loadUserByUsername: {}",username);
        User user = userRepository.findByLogin(username.toLowerCase()).orElseThrow(() ->  new UsernameNotFoundException(username));
        LOGGER.info("loadUserByUsername: {} = {}",username,user);
        return new CryptUserPrincipal(user);
    }
}
