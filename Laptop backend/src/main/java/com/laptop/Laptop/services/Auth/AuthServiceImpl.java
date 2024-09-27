package com.laptop.Laptop.services.Auth;

import com.laptop.Laptop.dto.AuthenticationRequestDto;
import com.laptop.Laptop.dto.JWTAuthenticationResponse;
import com.laptop.Laptop.dto.SignUpRequetDto;
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.enums.Roles;
import com.laptop.Laptop.exceptions.CustomerAlreadyExistException;
import com.laptop.Laptop.repository.ShopRepository;
import com.laptop.Laptop.repository.UserRepository;
import com.laptop.Laptop.services.Jwt.UserService;
import com.laptop.Laptop.util.JwtUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements  AuthService{
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final ShopRepository shopRepository;
    private  final UserService userService;
    private final JwtUtils jwtUtils;


    @Override
    public User createUser(SignUpRequetDto signUpRequest) {
        Optional<User> optionalUser = userRepository.findByUsername(signUpRequest.getUserName());
        if (optionalUser.isPresent()) {
            throw new CustomerAlreadyExistException("User already registered with the given username: " + signUpRequest.getUserName());
        }

        // Fetch the shop that the user will belong to
        Shop shop = shopRepository.findById(signUpRequest.getShopId())
                .orElseThrow(() -> new IllegalStateException("Shop not found"));

        User user = User.builder()
                .username(signUpRequest.getUserName())
                .phone(signUpRequest.getPhone())
                .password(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()))
                .role(Roles.CASHIER)  // Default to CASHIER role
                .shop(shop)           // Associate with the shop
                .build();

        User createdUser = userRepository.save(user);
        return createdUser;
    }




    public JWTAuthenticationResponse createAuthToken(@RequestBody AuthenticationRequestDto authenticationRequest) {
        // Fetch user from the specified shop
        Optional<User> optionalUser = userRepository.findByUsernameAndShopId(authenticationRequest.getUserName(), authenticationRequest.getShopId());

        if (!optionalUser.isPresent()) {
            throw new RuntimeException("User not found or not associated with the specified shop");
        }

        User user = optionalUser.get();
        try {
            // Authenticate the user with username and password
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUserName(),
                    authenticationRequest.getPassword()));
        } catch (RuntimeException e) {
            throw new RuntimeException("Incorrect login credentials");
        }

        // Load user details and generate JWT token with shopId
        UserDetails userDetails = userService.userDetailsService().loadUserByUsername(authenticationRequest.getUserName());
        final String jwt = jwtUtils.generateToken(userDetails.getUsername(), authenticationRequest.getShopId());

        // Return the JWT token, username, role, and shopId in the response
        return new JWTAuthenticationResponse(jwt, user.getUsername(), user.getRole().name());
    }





}
