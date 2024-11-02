package com.laptop.Laptop.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.laptop.Laptop.enums.ShopStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shopName;
    private String owner;
    private String shopCode;
    private String phoneNumber;
    private String address;
    private String description;
    private LocalDate registrationDate;
    private LocalDate expiryDate;
    
    @Enumerated(EnumType.STRING)
    private ShopStatus shopStatus;
    
    private String logo;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference // Use @JsonManagedReference here
    private List<User> users = new ArrayList<>(); // Initialize the list here

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Consider using LAZY for investments
    @JsonManagedReference // Use @JsonManagedReference for investments as well
    private List<Investment> investments = new ArrayList<>(); // Initialize the list here
}
