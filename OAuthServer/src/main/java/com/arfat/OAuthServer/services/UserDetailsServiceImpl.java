package com.arfat.OAuthServer.services;

import com.arfat.OAuthServer.modals.User;
import com.arfat.OAuthServer.modals.UserDetailsDTO;
import com.arfat.OAuthServer.repositories.UserDetailsRepository;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Arfat Bin Kileb
 * Created at 06-09-2020 01:54 PM
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserDetailsRepository userDetailsRepository;

    public UserDetailsServiceImpl(UserDetailsRepository userDetailsRepository) {
        this.userDetailsRepository = userDetailsRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> optionalUser = userDetailsRepository.findByUsername(username);
        optionalUser.orElseThrow(() -> new UsernameNotFoundException("Username or password incorrect"));

        UserDetails userDetails = new UserDetailsDTO(optionalUser.get());

        new AccountStatusUserDetailsChecker().check(userDetails);

        return userDetails;
    }
}
