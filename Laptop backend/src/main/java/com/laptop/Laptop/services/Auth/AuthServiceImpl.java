package com.laptop.Laptop.services.Auth;

import com.laptop.Laptop.dto.AuthenticationRequestDto;
import com.laptop.Laptop.dto.JWTAuthenticationResponse;
import com.laptop.Laptop.dto.SignUpRequetDto;
import com.laptop.Laptop.dto.UserUpdateRequestDto;
import com.laptop.Laptop.entity.Employee;
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.enums.Roles;
import com.laptop.Laptop.enums.ShopStatus;
import com.laptop.Laptop.exceptions.CustomerAlreadyExistException;
import com.laptop.Laptop.exceptions.ShopNotActivatedException;
import com.laptop.Laptop.exceptions.ShopNotFoundException;
import com.laptop.Laptop.exceptions.UnauthorizedActionException;
import com.laptop.Laptop.repository.EmployeeRepository;
import com.laptop.Laptop.repository.ShopRepository;
import com.laptop.Laptop.repository.UserRepository;
import com.laptop.Laptop.services.Jwt.UserService;
import com.laptop.Laptop.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

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


    public User createUser(SignUpRequetDto signUpRequest) {
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
                .password(new BCryptPasswordEncoder().encode(signUpRequest.getPassword())) // Encrypt the password
                .role(Roles.CASHIER) // Default role for new users (can be dynamic if needed)
                .shopCode(adminShopCode) // Assign the admin's shop code to the new user
                .shop(shop) // Associate the user with the admin's shop
                .build();

        // Save the new user to the database
        userRepository.save(user);

        // Create a new Employee and associate them with the same shop and user
        Employee employee = Employee.builder()
                .name(user.getUsername())
                .user(user) // Associate the Employee with the newly created user
                .shop(shop) // Associate the Employee with the same shop
                .build();

        // Save the employee to the database
        employeeRepository.save(employee);

        return user;
    }

    public User updateUserDetails(UserUpdateRequestDto updateRequest) {
        // Fetch the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get the username of the authenticated user

        // Fetch the user from the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Update phone if provided
        if (updateRequest.getUserName() != null && !updateRequest.getUserName().isEmpty()) {
            user.setPhone(updateRequest.getUserName());
        }

        // Update password if provided and current password is correct
        if (updateRequest.getNewPassword() != null && !updateRequest.getNewPassword().isEmpty()) {
            if (!passwordEncoder.matches(updateRequest.getCurrentPassword(), user.getPassword())) {
                throw new RuntimeException("Current password is incorrect");
            }
            user.setPassword(passwordEncoder.encode(updateRequest.getNewPassword()));
        }

        // Save the updated user
        return userRepository.save(user);
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
                        return new RuntimeException("User not found or not associated with the specified shop");
                    });
            System.out.println("Success: User found - " + user.getUsername());

            // Check if the shop is activated
            if (user.getShop().getShopStatus() != ShopStatus.ACTIVE) {
                System.out.println("Error: Shop is inactive for user: " + user.getUsername());
                throw new ShopNotActivatedException("Shop is not activated." +"\n" +
                        " Please contact the Service provider administrator." + "\n" +
                        "Name: Kenneth" + "\n" +
                        "Phone: 0711766223");
            }
            System.out.println("Success: Shop is active for user - " + user.getUsername());

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
        System.out.println("token generated is:"+ jwt);
        System.out.println("Success: Generated JWT token for user - " + userDetails.getUsername());

        // Return JWT response with token, username, role, and shopCode
        return new JWTAuthenticationResponse(jwt, user.getUsername(), user.getRole().name(), authenticationRequest.getShopCode(), user.getShop().getId());
    }














}
