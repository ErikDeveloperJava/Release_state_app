package com.example.realestate.repository;

import com.example.realestate.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image,Long> {

    List<Image> findAllByListingId(long id);
}
