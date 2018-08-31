package com.example.realestate.form;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserForm {

    @Length(min = 2,max = 255,message = "in field name wrong data")
    private String name;

    @Length(min = 4,max = 255,message = "in field email wrong data")
    private String email;

    @Length(min = 4,max = 255,message = "in field password wrong data")
    private String password;

    private MultipartFile image;
}