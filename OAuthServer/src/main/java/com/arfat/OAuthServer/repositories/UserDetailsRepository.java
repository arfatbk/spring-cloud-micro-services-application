package com.arfat.OAuthServer.repositories;

import com.arfat.OAuthServer.modals.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Arfat Bin Kileb
 * Created at 06-09-2020 01:57 PM
 */
public interface UserDetailsRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

}
