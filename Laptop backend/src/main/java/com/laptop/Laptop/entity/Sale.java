package com.laptop.Laptop.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private double salePrice;
    private int quantity;
    private String shopCode;
    private String salePerson;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private double saleTotal;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SaleItem> saleItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    private double totalPrice;  // Total price for all items in the sale

}
