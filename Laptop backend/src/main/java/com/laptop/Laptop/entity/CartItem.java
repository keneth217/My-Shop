package com.laptop.Laptop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.laptop.Laptop.enums.CartStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor  // Add this
@AllArgsConstructor //
@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private Cart cart;

    @ManyToOne
    @JsonIgnore
    private Product product;

    private int quantity;
    private  String shopCode;
    private double itemCosts;
    @Enumerated(EnumType.STRING)
    private CartStatus status;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "shop_id")
    private Shop shop;  // Each product belongs to one shop

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;
}
