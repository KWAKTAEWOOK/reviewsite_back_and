package com.project.reviewSite_backend.user.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.reviewSite_backend.answer.Answer;
import com.project.reviewSite_backend.user.UserRole;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userid;

    @Column(nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;


    public void update(String password1, String password2) {
        this.password = password1;
    }
}
