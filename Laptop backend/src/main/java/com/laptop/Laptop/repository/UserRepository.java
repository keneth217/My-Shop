package com.laptop.Laptop.repository;



import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsernameAndShopId(String username, Long shopId);  // Query users by shopId
    Optional<User> findByusername(String username);
    User findByRole(Roles role);

    Optional<User> findByUsername(String userName);
}
