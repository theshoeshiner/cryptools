package org.thshsh.crypt.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thshsh.crypt.User;
import org.thshsh.crypt.UserRepository;

//@Service
public class CryptUserDetailsService implements UserDetailsService {

	public static final Logger LOGGER = LoggerFactory.getLogger(CryptUserDetailsService.class);

    //@Autowired
    private UserRepository userRepository;

    public CryptUserDetailsService(UserRepository ur) {
        this.userRepository = ur;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {

        User user = userRepository.findByEmailEqualsOrUserNameEquals(username.toLowerCase(),username.toLowerCase()).orElseThrow(() ->  new UsernameNotFoundException(username));
        LOGGER.info("loadUserByUsername: {} = {}",username,user);
        return new CryptUserPrincipal(user);
    }
}
