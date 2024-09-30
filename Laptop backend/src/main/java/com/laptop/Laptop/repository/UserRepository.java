package com.laptop.Laptop.repository;



import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query("SELECT u FROM User u WHERE u.username = ?1 AND u.shop.id = ?2")
    Optional<User> findByUsernameAndShopId(String username, Long shopId);

   // Optional<User> findByUsernameAndShopId(String username, Long shopId);  // Query users by shopId
   Optional<User> findByUsernameAndShopCode(String username, String shopCode);
    User findByRole(Roles role);
    Optional<User> findByUsername(String userName);
    Optional<User> findByUsernameAndShopIdAndShopCode(String username, Long shopId, String shopCode);
    long countByShop(Shop shop);
    Optional<User> findByUsernameAndShop(String username, Shop shop);
}
