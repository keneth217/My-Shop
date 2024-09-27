package com.laptop.Laptop.services.Jwt;


import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                // This method uses only username to load user details
                return userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User Not found"));
            }
        };
    }

    // New method to load user by username and shopId
    public User loadUserByUsernameAndShopId(String username, Long shopId,String shopCode) {
        return userRepository.findByUsernameAndShopIdAndShopCode(username, shopId,shopCode)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with the specified shop"));
    }
}
