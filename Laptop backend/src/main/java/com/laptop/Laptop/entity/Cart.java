package com.laptop.Laptop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.enums.CartStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shopCode;


    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CartItem> items = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private CartStatus status;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "shop_id")
    private Shop shop;

    private double totalCart;  // Store the total cart value

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;

    public Cart(User loggedInUser) {
    }


    public void recalculateTotal() {
        // Calculate the total only for items in this cart
        totalCart = items.stream()
                .mapToDouble(item -> item.getQuantity() * item.getProduct().getSellingPrice())
                .sum();
    }
}
