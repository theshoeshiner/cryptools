package org.thshsh.crypt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thshsh.crypt.web.SecurityConfiguration;

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
