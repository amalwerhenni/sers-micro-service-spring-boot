package com.users.entity;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity
public class Roles {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long role_id;

    @Column(unique = true)
    private String role;

}
