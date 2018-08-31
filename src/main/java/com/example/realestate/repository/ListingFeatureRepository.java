package com.example.realestate.repository;

import com.example.realestate.model.ListingFeatures;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingFeatureRepository extends JpaRepository<ListingFeatures, Long> {

    List<ListingFeatures> findAllByListings_Id(long listingId);
}