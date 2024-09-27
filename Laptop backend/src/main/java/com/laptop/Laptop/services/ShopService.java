package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.ShopRegistrationRequestDto;
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.enums.Roles;
import com.laptop.Laptop.repository.ShopRepository;
import com.laptop.Laptop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ShopService {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // For encoding passwords

    public Shop registerShop(ShopRegistrationRequestDto request) {
        // Create the shop
        Shop shop = new Shop();
        shop.setName(request.getShopName());
        shop.setUniqueNumber(request.getShopUniqueIdentifier());

        // Create the admin user
        User adminUser = new User();
        adminUser.setUsername(request.getAdminUsername());
        adminUser.setPassword(passwordEncoder.encode(request.getAdminPassword()));
        adminUser.setRole(Roles.ADMIN);
        adminUser.setShop(shop);

        // Add the admin user to the shop's user list
        shop.getUsers().add(adminUser); // This should now work without errors

        // Save the shop and admin user
        return shopRepository.save(shop);
    }


}
