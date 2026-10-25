package com.astravastra.cart_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.astravastra.cart_service.entity.CartItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

	Optional<CartItem> findByUserIdAndVariantId(Long userId, Long variantId);
	
	List<CartItem> findByUserId(Long userId);
	
}
