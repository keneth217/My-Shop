package com.laptop.Laptop.services.Auth;

import com.laptop.Laptop.dto.AuthenticationRequestDto;
import com.laptop.Laptop.dto.Responses.JWTAuthenticationResponse;
import com.laptop.Laptop.dto.Responses.ShopResponse;
import com.laptop.Laptop.dto.Responses.UserResponse;
import com.laptop.Laptop.dto.SignInRequestDto;
import com.laptop.Laptop.dto.SignUpRequestDto;
import com.laptop.Laptop.dto.UserUpdateRequestDto;
import com.laptop.Laptop.entity.Employee;
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.enums.Roles;
import com.laptop.Laptop.enums.ShopStatus;
import com.laptop.Laptop.exceptions.CustomerAlreadyExistException;
import com.laptop.Laptop.exceptions.ShopNotActivatedException;
import com.laptop.Laptop.exceptions.UnauthorizedActionException;
import com.laptop.Laptop.repository.EmployeeRepository;
import com.laptop.Laptop.repository.ShopRepository;
import com.laptop.Laptop.repository.UserRepository;
import com.laptop.Laptop.services.Jwt.UserService;
import com.laptop.Laptop.services.ProductService;
import com.laptop.Laptop.util.JwtUtils;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements  AuthService{
    private final UserRepository userRepository;

    private  final EmployeeRepository employeeRepository;
    private final AuthenticationManager authenticationManager;
    private final ShopRepository shopRepository;
    private  final UserService userService;
    private final JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder; // For encoding passwords

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    @PostConstruct
    public void createAdmin() {
        User adminAccount = userRepository.findByRole(Roles.SUPER_USER); // Assuming Roles.ADMIN is the role
        System.out.println(adminAccount);

        if (adminAccount == null) {
            User newAdmin = new User();
            newAdmin.setFirstName("Keneth");
            newAdmin.setLastName("Admin");
            newAdmin.setPhone("0711766223");
            newAdmin.setUsername("superuser");

            newAdmin.setPassword(new BCryptPasswordEncoder().encode("admin"));
            newAdmin.setRole(Roles.SUPER_USER);  // Set the correct role

            userRepository.save(newAdmin);  // Save the new admin user
            System.out.println("New admin added");
        } else {
            System.out.println("Admin account already exists");
        }
    }
    public User createUser(SignUpRequestDto signUpRequest) {
        // Fetch the authenticated admin user from the SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Get the User object (which extends UserDetails) from the authentication context
        User adminDetails = (User) authentication.getPrincipal();

        // Extract admin details like username, role, shopId, and shopCode from the authenticated user (admin)
        String adminUsername = adminDetails.getUsername(); // Extract username from token
        Roles adminRole = adminDetails.getRole(); // Extract role from token
        Long adminShopId = adminDetails.getShop().getId(); // Extract shopId from token
        String adminShopCode = adminDetails.getShop().getShopCode(); // Extract shopCode from admin's shop

        // Ensure the user has an ADMIN role before proceeding
        if (!Roles.ADMIN.equals(adminRole)) {
            throw new UnauthorizedActionException("Only admins can create new users.");
        }

        // Fetch the shop based on the admin's shopId (ensure the shop exists)
        Shop shop = shopRepository.findById(adminShopId)
                .orElseThrow(() -> new IllegalStateException("Shop not found for the authenticated admin"));

        // Check if the user with the given username already exists
        Optional<User> optionalUser = userRepository.findByUsername(signUpRequest.getUserName());
        if (optionalUser.isPresent()) {
            throw new CustomerAlreadyExistException("User already registered with the given username: " + signUpRequest.getUserName());
        }

        // Create the new User and associate them with the admin's shop and shop code
        User user = User.builder()
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .username(signUpRequest.getUserName())

                .phone(signUpRequest.getPhone())
                .password(new BCryptPasswordEncoder().encode("password")) // Encrypt the password
                .role(Roles.CASHIER) // Default role for new users (can be dynamic if needed)
                .shopCode(adminShopCode) // Assign the admin's shop code to the new user
                .shop(shop) // Associate the user with the admin's shop
                .build();

        // Save the new user to the database
        userRepository.save(user);

        // Create a new Employee and associate them with the same shop and user
        Employee employee = Employee.builder()
                .name(user.getUsername())
                .phoneNumber(user.getPhone())
                .user(user) // Associate the Employee with the newly created user
                .shop(shop) // Associate the Employee with the same shop
                .build();

        // Save the employee to the database
        employeeRepository.save(employee);

        return user;
    }
    public UserUpdateRequestDto getLoggedInUserDetails() {
        // Fetch the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get the username of the authenticated user

        // Fetch the user from the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        String imageUrl = null;
        if (user.getUseImage() != null) {
            // Convert byte array to base64 or handle accordingly if you store it as a URL
            imageUrl = "data:image/png;base64," + Base64.getEncoder().encodeToString(user.getUseImage());
        }

        // Return the user details as a DTO using builder pattern
        return UserUpdateRequestDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .userName(user.getUsername())
                .shopCode(user.getShopCode())
                .UserImageUrl(imageUrl)
                .role(user.getRole()) // assuming the role is set in the User entity
                .build(); // Build the DTO object
    }
    public UserUpdateRequestDto updateUserDetails(UserUpdateRequestDto updateRequest) {
        // Fetch the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get the username of the authenticated user

        // Fetch the user from the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Update user image if provided
        if (updateRequest.getUserImage() != null && !updateRequest.getUserImage().isEmpty()) {
            try {
                // Get the image bytes from the uploaded file
                byte[] logoBytes = updateRequest.getUserImage().getBytes(); // Assuming userImage is a MultipartFile in the request
                user.setUseImage(logoBytes); // Set the byte array in the user entity
            } catch (IOException e) {
                logger.error("Error while uploading user image", e);
                throw new RuntimeException("Failed to store user image", e);
            }
        }

        // Update phone if provided and not empty
        if (updateRequest.getPhone() != null && !updateRequest.getPhone().isEmpty()) {
            user.setPhone(updateRequest.getPhone());
        }

        // Update first name if provided and not empty
        if (updateRequest.getFirstName() != null && !updateRequest.getFirstName().isEmpty()) {
            user.setFirstName(updateRequest.getFirstName());
        }

        // Update last name if provided and not empty
        if (updateRequest.getLastName() != null && !updateRequest.getLastName().isEmpty()) {
            user.setLastName(updateRequest.getLastName());
        }

        // Update password if provided and current password is correct
        if (updateRequest.getNewPassword() != null && !updateRequest.getNewPassword().isEmpty()) {
            // Check if the current password is correct before updating
            if (!passwordEncoder.matches(updateRequest.getCurrentPassword(), user.getPassword())) {
                throw new RuntimeException("Current password you entered is incorrect");
            }
            // Set the new password
            user.setPassword(passwordEncoder.encode(updateRequest.getNewPassword()));
        }

        // Convert user image to base64 if available
        String imageUrl = null;
        if (user.getUseImage() != null) {
            // Convert the byte array to base64
            imageUrl = "data:image/png;base64," + Base64.getEncoder().encodeToString(user.getUseImage());
        }

        // Save the updated user
        user = userRepository.save(user);

        // Convert the saved User to UserUpdateRequestDto using the builder pattern
        return UserUpdateRequestDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .shopCode(user.getShopCode())
                .userName(user.getUsername())
                .UserImageUrl(imageUrl)
                .role(user.getRole()) // Assuming the role is set in the User entity
                .build(); // Build and return the DTO object
    }
    public JWTAuthenticationResponse createAuthToken(AuthenticationRequestDto authenticationRequest) {
        System.out.println("Received authentication request: " + authenticationRequest);

        User user;
        try {
            String shopCode = authenticationRequest.getShopCode();
            System.out.println("Using shopCode: " + shopCode);

            // Fetch user by username and shopCode
            user = userRepository.findByUsernameAndShopCode(authenticationRequest.getUserName(), shopCode)
                    .orElseThrow(() -> {
                        System.out.println("Error: User not found for username: " + authenticationRequest.getUserName() + " and shopCode: " + shopCode);
                        return new UsernameNotFoundException("User not found or not associated with the specified shop");
                    });
            System.out.println("Success: User found - " + user.getUsername());

            try {
                // Check if the shop is activated
                if (user.getShop().getShopStatus() != ShopStatus.ACTIVE) {
                    System.out.println("Error: Shop is inactive for user: " + user.getUsername());
                    throw new ShopNotActivatedException("Shop is not activated.\n" +
                            "Please contact the Service provider administrator.\n" +
                            "Name: Keneth Kipyegon.\n" +
                            "Phone: 0711766223");
                }
                System.out.println("Success: Shop is active for user - " + user.getUsername());

            } catch (ShopNotActivatedException e) {
                throw e; // rethrow specific exception
            } catch (Exception e) {
                System.out.println("Error fetching user or checking shop status: " + e.getMessage());
                throw new UsernameNotFoundException("User not found or shop is inactive", e);
            }

            // Authenticate user
            try {
                // Verify the password using the password encoder
                if (!passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
                    System.out.println("Warning: Incorrect password for user: " + authenticationRequest.getUserName());
                    throw new RuntimeException("Incorrect login credentials");
                }
                System.out.println("Success: Password matches for user - " + user.getUsername());
            } catch (Exception e) {
                System.out.println("Error during password validation: " + e.getMessage());
                throw new RuntimeException("Incorrect login credentials", e);
            }

            // Load user details and generate JWT with shopCode
            UserDetails userDetails;
            try {
                userDetails = userService.userDetailsService().loadUserByUsername(authenticationRequest.getUserName());
                System.out.println("Success: User details loaded for - " + userDetails.getUsername());
            } catch (UsernameNotFoundException e) {
                System.out.println("Error: User not found in userDetailsService: " + e.getMessage());
                throw new UsernameNotFoundException("User not found in ,check Login details", e);
            }

            // Generate the JWT
            final String jwt = jwtUtils.generateToken(userDetails.getUsername(), user.getShop().getId(), authenticationRequest.getShopCode());
            System.out.println("token generated is:" + jwt);
            System.out.println("Success: Generated JWT token for user - " + userDetails.getUsername());
            String logoUrl = null;
            if (user.getShop().getShopLogo() != null) {
                // Convert byte array to base64 or handle accordingly if you store it as a URL
                logoUrl = "data:image/png;base64," + Base64.getEncoder().encodeToString(user.getShop().getShopLogo());
            }
            // Return JWT response with token, username, role, and shopCode
            JWTAuthenticationResponse jwtAuthenticationResponse = JWTAuthenticationResponse.builder()
                    .message("Login success")
                    .token(jwt)
                    .user(UserResponse.builder()
                            .shopId(user.getShop().getId())
                            .shopCode(authenticationRequest.getShopCode())
                            .role(user.getRole().name())
                            .username(user.getUsername())
                            .build())
                    .shop(ShopResponse.builder()
                            .shopName(user.getShop().getShopName())
                            .shopAddress(user.getShop().getAddress())
                            .shopLogo(logoUrl)
                            .shopCode(user.getShopCode())
                            .shopPhone(user.getShop().getPhoneNumber())
                            .build())
                    .build();
            return jwtAuthenticationResponse;
        } finally {

        }

    }
        public JWTAuthenticationResponse createSuperAuthToken (SignInRequestDto sign){
            System.out.println("Received authentication request: " + sign);

            try {
                // Fetch the user by username
                User user = userRepository.findByUsername(sign.getUserName())
                        .orElseThrow(() -> {
                            System.out.println("Error: User not found for username: " + sign.getUserName());
                            return new RuntimeException("User not found or not associated with the specified shop");
                        });

                System.out.println("Success: User found - " + user.getUsername());

                // Check if the user is associated with a shop
                Shop shop = user.getShop(); // Assuming user has a reference to shop
                if (shop != null) {
                    System.out.println("Error: User associated with a shop. Login not allowed.");
                    throw new RuntimeException("You are not allowed to log in to this site");
                }

                System.out.println("No shop associated with the user. Proceeding with login.");

                // Validate the user's password
                if (!passwordEncoder.matches(sign.getPassword(), user.getPassword())) {
                    System.out.println("Warning: Incorrect password for user: " + sign.getUserName());
                    throw new RuntimeException("Incorrect login credentials");
                }

                System.out.println("Success: Password matches for user - " + user.getUsername());

                // Load user details for JWT generation
                UserDetails userDetails = userService.userDetailsService().loadUserByUsername(sign.getUserName());
                System.out.println("Success: User details loaded for - " + userDetails.getUsername());

                // Generate the JWT token
                final String jwt = jwtUtils.generateSuperToken(userDetails.getUsername());
                System.out.println("Token generated: " + jwt);

                // Build the JWT response
                JWTAuthenticationResponse jwtAuthenticationResponse = JWTAuthenticationResponse.builder()
                        .message("Login success")
                        .token(jwt)
                        .user(UserResponse.builder()
                                .username(user.getUsername())
                                .role(user.getRole().name())
                                .shopId(null)  // No shop info since login only allowed if shop is null
                                .shopCode(null)
                                .build())
                        .shop(null)  // No shop details in response
                        .build();

                return jwtAuthenticationResponse;

            } catch (UsernameNotFoundException e) {
                System.out.println("Error: User not found in userDetailsService: " + e.getMessage());
                throw new UsernameNotFoundException("User not found, check login details", e);
            } catch (Exception e) {
                System.out.println("Error during authentication: " + e.getMessage());
                throw new RuntimeException("Authentication failed", e);
            }
        }

    public List<UserUpdateRequestDto> getUsersForLoggedInUserShop() {
        // Fetch the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get the username of the authenticated user

        // Fetch the user from the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Retrieve the shop associated with the user
        Shop shop = user.getShop();
        if (shop == null) {
            throw new RuntimeException("The logged-in user is not associated with any shop.");
        }

        // Find all users associated with the same shop
        List<User> users = userRepository.findAllByShop(shop);

        // Convert each user to UserUpdateRequestDto
        List<UserUpdateRequestDto> userDtos = users.stream().map(u -> {
            String imageUrl = null;
            if (u.getUseImage() != null) {
                imageUrl = "data:image/png;base64," + Base64.getEncoder().encodeToString(u.getUseImage());
            }
            return UserUpdateRequestDto.builder()
                    .firstName(u.getFirstName())
                    .lastName(u.getLastName())
                    .phone(u.getPhone())
                    .userName(u.getUsername())
                    .shopCode(shop.getShopCode()) // Shop code is the same for all users in this shop
                    .UserImageUrl(imageUrl)
                    .role(u.getRole())
                    .build();
        }).collect(Collectors.toList());

        return userDtos;
    }

    public UserUpdateRequestDto updateUserRole(String username, String newRole) {
        // Fetch the user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if the provided role is valid (if using an enum for roles)
        Roles role;
        try {
            role = Roles.valueOf(newRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role specified");
        }

        // Update the user's role
        user.setRole(role);
        user = userRepository.save(user);

        // Map the updated user to a DTO to return updated details
        return UserUpdateRequestDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .userName(user.getUsername())
                .shopCode(user.getShopCode())
                .role(user.getRole())
                .build();
    }


}
