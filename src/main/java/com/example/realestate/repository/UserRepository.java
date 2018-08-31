package com.example.realestate.repository;

import com.example.realestate.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("select u from User u order by size(u.listings) desc ")
    List<User> findTop3Populars(Pageable pageable);

    boolean existsByEmail(String email);

    @Query("select u from User u where u.id != :id")
    List<User> findAllByIdNotEquals(@Param("id") long id, Pageable pageable);
}