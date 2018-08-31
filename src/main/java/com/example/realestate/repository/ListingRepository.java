package com.example.realestate.repository;

import com.example.realestate.model.Listing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {

    List<Listing> findTop4ByOrderByIdDesc();

    List<Listing> findAllByTitleContains(String title,Pageable pageable);

    int countByTitleContains(String title);

    List<Listing> findAllByUserId(long userId, Pageable pageable);

    List<Listing> findAllByUserId(long userId);

    int countByUserId(long userId);
}