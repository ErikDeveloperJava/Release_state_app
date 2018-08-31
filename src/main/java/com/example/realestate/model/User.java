package com.example.realestate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user")
@ToString(exclude = "listings")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String email;

    private String password;

    private String picUrl;

    @Enumerated(EnumType.STRING)
    private UserType userType = UserType.USER;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Listing> listings;
}
