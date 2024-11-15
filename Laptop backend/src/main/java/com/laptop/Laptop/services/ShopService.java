package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.EmailDetails;
import com.laptop.Laptop.dto.ShopRegistrationRequestDto;
import com.laptop.Laptop.dto.ShopUpdateRequestDto;
import com.laptop.Laptop.emails.EmailService;
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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ShopService {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder; // For encoding passwords

    @Transactional
    public Shop registerShop(ShopRegistrationRequestDto request) {
        String uniqueCode = generateUniqueCode();

        // Create the shop
        Shop shop = new Shop();
        shop.setShopName(request.getShopName());
        shop.setShopCode(uniqueCode);
        System.out.println(uniqueCode); // Generate unique code
        shop.setAddress(request.getAddress());
        shop.setOwner(request.getOwner());
        shop.setEmail(request.getEmail());
        shop.setPhoneNumber(request.getPhoneNumber());
        shop.setShopStatus(ShopStatus.ACTIVE); // Initially set to ACTIVE
        LocalDate registerDate = LocalDate.now();
        shop.setDescription("New Installation");
        shop.setRegistrationDate(registerDate);

        // Set the expiry date to two weeks from the registration date
        LocalDate expiryDate = registerDate.plusWeeks(2);
        shop.setExpiryDate(expiryDate);

        // Check if the username is already registered globally (for any shop)
        Optional<User> optionalUser = userRepository.findByUsername(request.getAdminUsername());
        if (optionalUser.isPresent()) {
            throw new CustomerAlreadyExistException("This Username has already been used: " + request.getAdminUsername());
        }

        // Save the shop first before creating users and employees
        shopRepository.save(shop);

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

        // Save the admin user
        userRepository.save(adminUser);

        // Create a new Employee and associate them with the same shop and user
        Employee employee = Employee.builder()
                .name(request.getAdminUsername())
                .phoneNumber(shop.getPhoneNumber())
                .user(adminUser) // Associate the Employee with the newly created user
                .shop(shop) // Associate the Employee with the same shop
                .build();

        // Save the employee to the database
        employeeRepository.save(employee);

        // Prepare email details
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(request.getEmail());
        emailDetails.setSubject("Shop Management Manual PDF");

        // Construct the message body with shop details
        String messageBody = "Dear " + request.getOwner() + ",\n\n" +
                "Thank you for registering your shop with us.\n\n" +
                "Here are your shop details:\n" +
                "Shop Name: " + request.getShopName() + "\n" +
                "Shop Code: " + uniqueCode + "\n" +
                "Address: " + request.getAddress() + "\n" +
                "Owner: " + request.getOwner() + "\n" +
                "Phone Number: " + request.getPhoneNumber() + "\n" +
                "Registration Date: " + registerDate + "\n" +
                "Expiry Date: " + expiryDate + "\n\n" +
                "Your login credentials are:\n" +
                "Shop Code: " + uniqueCode + "\n" +
                "Username: " + request.getAdminUsername() + "\n" +
                "Password: admin\n\n" +
                "Please find the Shop Management PDF attached.\n\n" +
                "In case of any issue, contact us via:\n\n" +
                "Phone: 0711766223\n" +
                "Email: kipyegonkeneth03@gmail.com\n\n" +
                "Best Regards,\n" +
                "The Shop Management Team";

// Set the message body
        emailDetails.setMessageBody(messageBody);


        // Attach the Shop Management PDF
        emailDetails.setAttachment("SHOP MANAGEMENT.PDF");

        // Send the email with attachment
        emailService.sendEmailAlertWithAttachment(emailDetails);

        // Return the saved shop
        return shop;
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
        shop.setDescription(request.getDescription());

// Save and return the updated shop
        return shopRepository.save(shop);
    }


    public Shop deactiveShop(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException("Shop not found"));

// Deactivate the shop
        shop.setExpiryDate(LocalDate.now());
        shop.setShopStatus(ShopStatus.INACTIVE);
        System.out.println("Shop status set to INACTIVE.");

// Save and return the updated shop
        return shopRepository.save(shop);
    }


    // List all registered shops
    public List<ShopUpdateRequestDto> getAllShops() {
// Fetch all shops from the repository
        List<Shop> shops = shopRepository.findAll();

// Map each Shop entity to ShopUpdateRequestDto
        return shops.stream()
                .map(shop -> ShopUpdateRequestDto.builder()
                        .id(shop.getId())
                        .shopName(shop.getShopName())
                        .address(shop.getAddress())
                        .owner(shop.getOwner())
                        .email(shop.getEmail())
                        .shopCode(shop.getShopCode())
                        .phoneNumber(shop.getPhoneNumber())
                        .description(shop.getDescription())
                        .registrationDate(shop.getRegistrationDate())
                        .expiryDate(shop.getExpiryDate())
                        .status(shop.getShopStatus())

                        .build())
                .collect(Collectors.toList());
    }


    // Get all shops by status
    public List<Shop> getAllShopsByStatus(ShopStatus status) {
        List<Shop> shops = shopRepository.findByShopStatus(status);
        return shops; // Fetch shops by status
    }

    @Transactional
    public ShopUpdateRequestDto updateShopDetails(Long shopId, ShopUpdateRequestDto request) {

// Fetch the shop by its ID
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException("Shop not found with ID: " + shopId));

// Update shop details
        shop.setShopName(request.getShopName());
        shop.setAddress(request.getAddress());
        shop.setOwner(request.getOwner());

        shop.setPhoneNumber(request.getPhoneNumber());
        shop.setDescription(request.getDescription());

// Update logo URL if provided
        String logoUrl = null;
        if (shop.getShopLogo() != null) {
// Convert byte array to base64 or handle accordingly if you store it as a URL
            logoUrl = "data:image/png;base64," + Base64.getEncoder().encodeToString(shop.getShopLogo());
        }

// Convert and store logo as byte array if a new logo file is provided
        if (request.getShopLogo() != null && !request.getShopLogo().isEmpty()) {
            try {
                byte[] logoBytes = request.getShopLogo().getBytes();
                shop.setShopLogo(logoBytes);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store shop logo", e);
            }
        }

// Save updated shop details
        shopRepository.save(shop);

// Map the updated shop to ShopUpdateRequestDto to return
        ShopUpdateRequestDto response = ShopUpdateRequestDto.builder()
                .shopName(shop.getShopName())
                .address(shop.getAddress())
                .email(shop.getEmail())
                .owner(shop.getOwner())
                .phoneNumber(shop.getPhoneNumber())
                .description(shop.getDescription())
                .shopLogoUrl(logoUrl)
                .build();

        return response;
    }

}



