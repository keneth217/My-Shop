package com.laptop.Laptop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private String email;
    private String description;
    private LocalDate registrationDate;
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    private ShopStatus shopStatus;

 //   @Lob  // Use @Lob to store large objects like images
   // private String shopLogoUrl;

    @Column(columnDefinition = "longblob")
    private byte[] shopLogo;  // Storing images as byte arrays

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Use @JsonManagedReference here
    private List<User> users = new ArrayList<>(); // Initialize the list here

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Use @JsonManagedReference for investments as well
    private List<Investment> investments = new ArrayList<>(); // Initialize the list here

    // Override toString method to prevent issues with logging
    @Override
    public String toString() {
        return "Shop{" +
                "id=" + id +
                ", shopName='" + shopName + '\'' +
                ", owner='" + owner + '\'' +
                ", shopCode='" + shopCode + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", description='" + description + '\'' +
                ", registrationDate=" + registrationDate +
                ", expiryDate=" + expiryDate +
                ", shopStatus=" + shopStatus +
                '}';
    }
}
