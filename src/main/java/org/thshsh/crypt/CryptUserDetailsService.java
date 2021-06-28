package org.thshsh.crypt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//@Service
public class CryptUserDetailsService implements UserDetailsService {

    //@Autowired
    private UserRepository userRepository;

    public CryptUserDetailsService(UserRepository ur) {
		this.userRepository = ur;
	}

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByEmailEqualsOrUserNameEquals(username.toLowerCase(),username.toLowerCase()).orElseThrow(() ->  new UsernameNotFoundException(username));
        return new CryptUserPrincipal(user);
    }
}