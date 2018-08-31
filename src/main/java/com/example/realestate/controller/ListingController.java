package com.example.realestate.controller;

import com.example.realestate.form.ListingForm;
import com.example.realestate.model.CurrentUser;
import com.example.realestate.model.Image;
import com.example.realestate.model.Listing;
import com.example.realestate.model.ListingFeatures;
import com.example.realestate.repository.ImageRepository;
import com.example.realestate.repository.ListingFeatureRepository;
import com.example.realestate.repository.ListingRepository;
import com.example.realestate.util.ImageUtil;
import com.example.realestate.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
public class ListingController {

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private ListingFeatureRepository listingFeatureRepository;

    @Autowired
    private PaginationUtil paginationUtil;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageUtil imageUtil;

    @GetMapping("/listing/add")
    public String addGet(Model model, @AuthenticationPrincipal CurrentUser currentUser) {
        model.addAttribute("currentUser",currentUser.getUser());
        model.addAttribute("features", listingFeatureRepository.findAll());
        model.addAttribute("form",new ListingForm());
        return "addListing";
    }

    @PostMapping("/listing/add")
    public String addPost(@Valid @ModelAttribute("form")ListingForm form, BindingResult result,
                          @AuthenticationPrincipal CurrentUser currentUser,Model model){
        if(result.hasErrors()){
            model.addAttribute("features", listingFeatureRepository.findAll());
            model.addAttribute("currentUser",currentUser.getUser());
            return "addListing";
        }else if (form.getFeatures().size() == 0){
            result.addError(new FieldError("form","features","please choose a feature"));
            model.addAttribute("features", listingFeatureRepository.findAll());
            model.addAttribute("currentUser",currentUser.getUser());
            return "addListing";
        }else if(form.getImage().isEmpty()){
            result.addError(new FieldError("form","image","image is empty"));
            model.addAttribute("features", listingFeatureRepository.findAll());
            model.addAttribute("currentUser",currentUser.getUser());
            return "addListing";
        }else if(!imageUtil.isValidFormat(form.getImage().getContentType())){
            result.addError(new FieldError("form","image","invalid image format"));
            model.addAttribute("features", listingFeatureRepository.findAll());
            model.addAttribute("currentUser",currentUser.getUser());
            return "addListing";
        }else {
            List<ListingFeatures> features = listingFeatureRepository.findAllById(form.getFeatures());
            Listing listing = Listing.builder()
                     .title(form.getTitle())
                    .description(form.getDescription())
                    .price(form.getPrice())
                    .bedrooms(form.getBedrooms())
                    .bathrooms(form.getBathrooms())
                    .area(form.getArea())
                    .mlsNo(form.getMlsNo())
                    .listingType(form.getListingType())
                    .picUrl("")
                    .featureList(features)
                    .user(currentUser.getUser())
                    .build();
            listingRepository.save(listing);
            String img = System.currentTimeMillis() + form.getImage().getOriginalFilename();
            imageRepository.save(new Image(0,listing.getId() + "/" + img,listing));
            listing.setPicUrl(listing.getId() + "/" + img);
            try {
                imageUtil.save("listings\\" + listing.getId(),img,form.getImage());
                listingRepository.save(listing);
            }catch (Exception e){
                imageUtil.delete("listings\\" + listing.getId());
                throw new RuntimeException(e);
            }
            return "redirect:/";
        }
    }

    @GetMapping("/listings")
    public String listings(Pageable pageable,Model model,
                           @AuthenticationPrincipal CurrentUser currentUser){
        int length = paginationUtil.getLength((int) listingRepository.count(), pageable.getPageSize());
        pageable = paginationUtil.getCheckedPageable(pageable.getPageNumber(),pageable.getPageSize(),length);
        if(currentUser != null){
            model.addAttribute("currentUser",currentUser.getUser());
        }
        model.addAttribute("listings",listingRepository.findAll(pageable).getContent());
        model.addAttribute("pageNumber",pageable.getPageNumber());
        model.addAttribute("length",length);
        model.addAttribute("title","All listings");
        return "listings";
    }


    @GetMapping("/listing/search")
    public String searchListings(Pageable pageable, Model model,
                                 @AuthenticationPrincipal CurrentUser currentUser, @RequestParam("title")String title){
        if(title == null){
            title = "";
        }
        int length = paginationUtil.getLength(listingRepository.countByTitleContains(title), pageable.getPageSize());
        pageable = paginationUtil.getCheckedPageable(pageable.getPageNumber(),pageable.getPageSize(),length);
        if(currentUser != null){
            model.addAttribute("currentUser",currentUser.getUser());
        }
        model.addAttribute("listings",listingRepository.findAllByTitleContains(title,pageable));
        model.addAttribute("pageNumber",pageable.getPageNumber());
        model.addAttribute("length",length);
        model.addAttribute("title","Search by title: " + title);
        return "listings";
    }

    @GetMapping("/listing/{id}")
    public String listing(@PathVariable("id")String strId,Model model,
                          @AuthenticationPrincipal CurrentUser currentUser){
        try {
            Optional<Listing> optListing = listingRepository.findById(Long.parseLong(strId));
            if(!optListing.isPresent()){
                throw new NumberFormatException();
            }
            if(currentUser != null){
                model.addAttribute("currentUser",currentUser.getUser());
            }
            model.addAttribute("listing",optListing.get());
            model.addAttribute("images",imageRepository.findAllByListingId(optListing.get().getId()));
            model.addAttribute("features",listingFeatureRepository.findAllByListings_Id(optListing.get().getId()));
            return "listing_details";
        }catch (NumberFormatException e){
            return "redirect:/";
        }
    }

    @PostMapping("/listing/upload")
    public @ResponseBody
    Image upload(@RequestParam("listingId") long listingId,
                   @RequestParam("image")MultipartFile image){
        if(!image.isEmpty() && imageUtil.isValidFormat(image.getContentType())){
            String imgName = System.currentTimeMillis() + image.getOriginalFilename();
            Image img = Image.builder()
                    .url(listingId + "/" + imgName)
                    .listing(Listing.builder().id(listingId).build())
                    .build();
            imageRepository.save(img);
            try {
                imageUtil.save("listings\\" + listingId,imgName,image);
                return img;
            }catch (NumberFormatException e){
                imageUtil.delete("listings\\" + listingId  +"\\"+ imgName);
                return new Image();
            }
        }else {
            return new Image();
        }
    }

    @PostMapping("/listing/image/delete/{id}")
    public @ResponseBody
    boolean deleteImg(@PathVariable("id")long id){
        Optional<Image> img = imageRepository.findById(id);
        if(img.isPresent()){
            imageUtil.delete("listings/" + img.get().getUrl());
            imageRepository.delete(img.get());
            return true;
        }
        return false;
    }

    @PostMapping("/listing/change/main-img/{id}")
    public @ResponseBody
    boolean changeMainImg(@RequestParam("url")String url,@PathVariable("id")long id){
        Optional<Listing> listing = listingRepository.findById(id);
        if(listing.isPresent()){
            listing.get().setPicUrl(url);
            listingRepository.save(listing.get());
            return true;
        }
        return false;
    }

    @GetMapping("/listing/delete/{id}")
    public String delete(@PathVariable("id") long id){
        imageUtil.delete("listings\\" + id);
        listingRepository.deleteById(id);
        return "redirect:/";
    }
}