package com.pokemon.newsfeed.repository;

import com.pokemon.newsfeed.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    //Optional<User> findById(Long userId);
    //아래랑 겹치나..?
    Optional<User> findByUserId(String userId);
    Optional<User> findByEmail(String email);
}
