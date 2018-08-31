package com.example.realestate.controller;

import com.example.realestate.model.CurrentUser;
import com.example.realestate.model.Listing;
import com.example.realestate.model.User;
import com.example.realestate.repository.ListingRepository;
import com.example.realestate.repository.UserRepository;
import com.example.realestate.util.ImageUtil;
import com.example.realestate.util.PaginationUtil;
import com.sun.org.apache.xpath.internal.operations.Number;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageUtil imageUtil;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private PaginationUtil paginationUtil;

    @GetMapping("/users")
    public String users(@AuthenticationPrincipal CurrentUser currentUser,
                        Model model){
        if(currentUser != null){
            model.addAttribute("currentUser",currentUser.getUser());
            model.addAttribute("users",userRepository.findAllByIdNotEquals(currentUser.getUser().getId(),PageRequest.of(0,4)));
        }else {
            model.addAttribute("users",userRepository.findAll(PageRequest.of(0,4)).getContent());
        }
        return "users";
    }

    @GetMapping("/user/load/{page}")
    public @ResponseBody
    List<User> loadMore(@PathVariable("page")int page,@AuthenticationPrincipal CurrentUser currentUser){
        long id;
        if(currentUser == null){
            id = -1;
        }else {
            id = currentUser.getUser().getId();
        }
        return userRepository.findAllByIdNotEquals(id,PageRequest.of(page,4));
    }

    @PostMapping("/user/delete/{id}")
    public @ResponseBody
    boolean delete(@PathVariable("id") long id){
        Optional<User> optUser = userRepository.findById(id);
        if(optUser.isPresent()){
            deleteListingsImages(listingRepository.findAllByUserId(optUser.get().getId()));
            imageUtil.delete("users\\" + optUser.get().getId());
            userRepository.delete(optUser.get());
            return true;
        }
        return false;
    }

    private void deleteListingsImages(List<Listing> listings){
        for (Listing listing : listings) {
            imageUtil.delete("listings\\" + listing.getId());
        }
    }

    @GetMapping("/user/profile")
    public String profile(@AuthenticationPrincipal CurrentUser currentUser, Model model, Pageable pageable){
        model.addAttribute("currentUser",currentUser.getUser());
        int length = paginationUtil.getLength(listingRepository.countByUserId(currentUser.getUser().getId()),pageable.getPageSize());
        pageable = paginationUtil.getCheckedPageable(pageable.getPageNumber(),pageable.getPageSize(),length);
        model.addAttribute("listings",listingRepository.findAllByUserId(currentUser.getUser().getId(),pageable));
        model.addAttribute("length",length);
        model.addAttribute("pageNumber",pageable.getPageNumber());
        return "user_profile";
    }

    @PostMapping("/user/profile/image/change")
    public @ResponseBody
    String changeProfileImage(@RequestParam("image")MultipartFile multipartFile,
                              @AuthenticationPrincipal CurrentUser currentUser){
        String img = System.currentTimeMillis() + multipartFile.getOriginalFilename();
        User user = currentUser.getUser();
        user.setPicUrl(user.getId() + "/" + img);
        userRepository.save(user);
        try {
            imageUtil.save("users\\" + user.getId(),img,multipartFile);
            return user.getPicUrl();
        }catch (Exception e){
            imageUtil.delete(user.getId() + "\\" + img);
            throw new RuntimeException();
        }
    }

    @GetMapping("/user/profile/delete")
    public String userProfileDelete(@AuthenticationPrincipal CurrentUser user, HttpSession session){
        imageUtil.delete("users\\" + user.getUser().getId());
        deleteListingsImages(listingRepository.findAllByUserId(user.getUser().getId(),PageRequest.of(0,4)));
        userRepository.delete(user.getUser());
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/user/{id}")
    public String userDetails(@PathVariable("id")String strId,Model model,
                             @AuthenticationPrincipal CurrentUser currentUser,Pageable pageable){
        long id;
        try {
            id = Long.parseLong(strId);
            if(!userRepository.existsById(id)){
                throw new NumberFormatException();
            }else if(currentUser != null && currentUser.getUser().getId() == id){
                return "redirect:/user/profile";
            }
            if(currentUser != null){
                model.addAttribute("currentUser",currentUser);
            }
            model.addAttribute("user",userRepository.findById(id).get());
            int length = paginationUtil.getLength(listingRepository.countByUserId(id),pageable.getPageSize());
            pageable = paginationUtil.getCheckedPageable(pageable.getPageNumber(),pageable.getPageSize(),length);
            model.addAttribute("listings",listingRepository.findAllByUserId(id,pageable));
            model.addAttribute("length",length);
            model.addAttribute("pageNumber",pageable.getPageNumber());
            return "user_details";
        }catch (NumberFormatException e){
            return "redirect:/";
        }
    }
}