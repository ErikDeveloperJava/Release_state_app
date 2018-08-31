package com.example.realestate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String description;

    @Enumerated(value = EnumType.STRING)
    private ListingType listingType;

    private double price;

    private int bedrooms;

    private int bathrooms;

    private double area;

    private String mlsNo;

    @ManyToMany
    @JoinTable(name = "listing_feature",
            joinColumns = {@JoinColumn(name = "listing_id")},
            inverseJoinColumns = {@JoinColumn(name = "feature_id")})
    private List<ListingFeatures> featureList;

    private String picUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;
}
