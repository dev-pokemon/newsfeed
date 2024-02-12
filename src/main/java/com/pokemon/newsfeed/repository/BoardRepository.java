package com.pokemon.newsfeed.repository;

import com.pokemon.newsfeed.entity.Board;
import com.pokemon.newsfeed.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
   List<Board> findByUser(User user);

   List<Board> findAllByOrderByCreatedAtDesc();
}
