package com.example.realestate.controller;

import com.example.realestate.form.UserForm;
import com.example.realestate.model.User;
import com.example.realestate.model.UserType;
import com.example.realestate.repository.UserRepository;
import com.example.realestate.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Controller
public class LoginRegisterController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ImageUtil imageUtil;

    @GetMapping("/signIn")
    public String login(Model model){
        model.addAttribute("form",new UserForm());
        return "signin";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form")UserForm form, BindingResult result){
        if(result.hasErrors()){
            return "signin";
        }else if(userRepository.existsByEmail(form.getEmail())){
            result.addError(new FieldError("form","email","user with email '" +form.getEmail() +"' already exists"));
            return "signin";
        } else if(form.getImage().isEmpty()){
            result.addError(new FieldError("form","image","image is empty"));
            return "signin";
        }else if(!imageUtil.isValidFormat(form.getImage().getContentType())){
            result.addError(new FieldError("form","image","invalid image format"));
            return "signin";
        }else {
            User user = User.builder()
                    .name(form.getName())
                    .email(form.getEmail())
                    .password(passwordEncoder.encode(form.getPassword()))
                    .picUrl("")
                    .userType(UserType.USER)
                    .build();
            add(user,form.getImage());
        }
        return "redirect:/signIn";
    }

    private void add(User user, MultipartFile image){
        userRepository.save(user);
        String img = System.currentTimeMillis() + image.getOriginalFilename();
        user.setPicUrl(user.getId() + "/" + img);
        userRepository.save(user);
        try {
            imageUtil.save("users\\"+ user.getId(),img,image);
        }catch (Exception e){
            imageUtil.delete("users\\"+ user.getId());
            throw new RuntimeException(e);
        }
    }
}