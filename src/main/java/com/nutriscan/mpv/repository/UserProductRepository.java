package com.nutriscan.mpv.repository;

import com.nutriscan.mpv.Product;
import com.nutriscan.mpv.User;
import com.nutriscan.mpv.UserProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProductRepository extends JpaRepository<UserProduct, Long> {

    UserProduct findByUserAndProduct(User user, Product product);
}

