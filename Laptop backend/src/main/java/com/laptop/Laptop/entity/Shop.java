package com.laptop.Laptop.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@NoArgsConstructor  // Add this
@AllArgsConstructor //
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



    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    @JsonBackReference

    private List<User> users = new ArrayList<>(); // Initialize the list here


    @OneToMany(mappedBy = "shop")
    @JsonManagedReference // Forward reference
    private List<Investment> investments;

}
