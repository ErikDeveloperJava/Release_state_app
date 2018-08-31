package com.example.realestate.controller;

import com.example.realestate.model.CurrentUser;
import com.example.realestate.model.UserType;
import com.example.realestate.repository.ListingRepository;
import com.example.realestate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    @GetMapping("/")
    public String main(Model model, @AuthenticationPrincipal CurrentUser currentUser) {
        if (currentUser != null) {
            if(currentUser.getUser().getUserType().equals(UserType.ADMIN)){
                return "redirect:/users";
            }
            model.addAttribute("currentUser", currentUser.getUser());
        }
        model.addAttribute("listings",listingRepository.findTop4ByOrderByIdDesc());
        model.addAttribute("users",userRepository.findTop3Populars(PageRequest.of(0,3)));
        return "index";
    }
}
