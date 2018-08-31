package com.example.realestate.form;

import com.example.realestate.model.ListingType;
import lombok.*;
import org.hibernate.type.ListType;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingForm {

    @Length(min = 2,max = 255,message = "in field title wrong data")
    private String title;

    @Length(min = 2,max = 255,message = "in field title wrong data")
    private String description;

    @Range(min = 10,max = 100000000,message = "id field price wrong data")
    private double price;

    @Range(min = 0,max = 30,message = "id field bedrooms wrong data")
    private int bedrooms;

    @Range(min = 0,max = 30,message = "id field bathrooms wrong data")
    private int bathrooms;

    @Range(min = 2,max = 100000000,message = "id field area wrong data")
    private double area;

    @Length(min = 2,max = 255,message = "id field mls no wrong data")
    private String mlsNo;

    @NotNull(message = "please choose For Sale or Rant?")
    private ListingType listingType;

    private MultipartFile image;

    private List<Long> features = new ArrayList<>();
}