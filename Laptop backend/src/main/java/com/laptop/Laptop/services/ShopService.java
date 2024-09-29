package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.ShopRegistrationRequestDto;
import com.laptop.Laptop.entity.Employee;
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.enums.Roles;
import com.laptop.Laptop.enums.ShopStatus;
import com.laptop.Laptop.exceptions.CustomerAlreadyExistException;
import com.laptop.Laptop.exceptions.ShopNotFoundException;
import com.laptop.Laptop.repository.EmployeeRepository;
import com.laptop.Laptop.repository.ShopRepository;
import com.laptop.Laptop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ShopService {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // For encoding passwords

    public Shop registerShop(ShopRegistrationRequestDto request) {

        Optional<User> optionalUser = userRepository.findByUsername(request.getAdminUsername());
        if (optionalUser.isPresent()) {
            throw new CustomerAlreadyExistException("User already registered with the given username: " + request.getAdminUsername());
        }
        String uniqueCode = generateUniqueCode();

        // Create the shop
        Shop shop = new Shop();
        shop.setName(request.getShopName());
        shop.setShopCode(uniqueCode);
        System.out.println(uniqueCode); // Generate unique code
        shop.setAddress("1234 nakuru");
        shop.setOwner("keneth");
        shop.setPhoneNumber("0711766223");
        shop.setShopStatus(ShopStatus.ACTIVE); // Initially set to ACTIVE
        LocalDate registerDate = LocalDate.now();
        shop.setRegistrationDate(registerDate);

        // Set the expiry date to two weeks from registration date
        LocalDate expiryDate = registerDate.plusWeeks(2);
        shop.setExpiryDate(expiryDate);

        // Check if the current date is past the expiry date
        if (LocalDate.now().isAfter(expiryDate)) {
            shop.setShopStatus(ShopStatus.INACTIVE); // Set to INACTIVE if past expiry date
        }

        // Create the admin user
        User adminUser = new User();
        adminUser.setUsername(request.getAdminUsername());
        adminUser.setPassword(passwordEncoder.encode("admin"));
        adminUser.setRole(Roles.ADMIN);
        adminUser.setShopCode(uniqueCode);
        adminUser.setPhone(shop.getPhoneNumber());
        adminUser.setShop(shop);



        // Add the admin user to the shop's user list
        shop.getUsers().add(adminUser);

        shopRepository.save(shop);

        // Create a new Employee and associate them with the same shop and user
        Employee employee = Employee.builder()
                .name(request.getAdminUsername())
                .user(adminUser) // Associate the Employee with the newly created user
                .shop(shop) // Associate the Employee with the same shop
                .build();
        // Save the employee to the database
        employeeRepository.save(employee);

        // Save the shop and admin user and employee
        return  shop;

    }

    // Generate a unique 6-digit code starting with the letter 'S'
    private String generateUniqueCode() {
        Random random = new Random();
        // Generate a random 6-digit number
        int number = random.nextInt(1000000); // Generate a number between 0 and 999999
        // Format the number to ensure it has 5 digits with leading zeros if necessary
        String formattedNumber = String.format("%05d", number);
        // Combine 'S' with the 5-digit number
        return "S" + formattedNumber;
    }

    // Activate a shop by changing its status to active
    public Shop activateShop(Long shopId, ShopRegistrationRequestDto request) {
        // Validate the request expiry date before proceeding
        if (request.getExpiryDate() == null || request.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expiry date must be a future date.");
        }

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException("Shop not found"));

        // Check if the current date is past the expiry date
        if (LocalDate.now().isAfter(shop.getExpiryDate())) {
            shop.setShopStatus(ShopStatus.INACTIVE);
            System.out.println("Shop status set to INACTIVE due to expiry date.");
        } else {
            shop.setShopStatus(ShopStatus.ACTIVE);
            System.out.println("Shop status set to ACTIVE.");
        }

        // Update the expiry date based on the request
        shop.setExpiryDate(request.getExpiryDate());

        // Save and return the updated shop
        return shopRepository.save(shop);
    }


    public Shop deactiveShop(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException("Shop not found"));

        // Deactivate the shop
        shop.setShopStatus(ShopStatus.INACTIVE);
        System.out.println("Shop status set to INACTIVE.");

        // Save and return the updated shop
        return shopRepository.save(shop);
    }



    // List all registered shops
    public List<Shop> getAllShops() {
        List<Shop> shop= shopRepository.findAll(); // Fetch all shops
        return shop;
    }

    // Get all shops by status
    public List<Shop> getAllShopsByStatus(ShopStatus status) {
        List<Shop> shops=shopRepository.findByShopStatus(status);
        return  shops; // Fetch shops by status
    }
}



