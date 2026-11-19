package com.astravastra.cart_service.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.astravastra.cart_service.dto.ResponseDto;
import com.astravastra.cart_service.dto.SizeDto;
import com.astravastra.cart_service.dto.WishlistRequest;
import com.astravastra.cart_service.dto.WishlistResponse;
import com.astravastra.cart_service.entity.Product;
import com.astravastra.cart_service.entity.ProductImage;
import com.astravastra.cart_service.entity.ProductVariant;
import com.astravastra.cart_service.entity.Users;
import com.astravastra.cart_service.entity.Wishlist;
import com.astravastra.cart_service.repository.ProductRepository;
import com.astravastra.cart_service.repository.UserRepository;
import com.astravastra.cart_service.repository.WishlistRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishlistService {

	private final WishlistRepository wishlistRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	@Transactional
	public ResponseDto addToWishlist(String userEmail, WishlistRequest request) {

		Users user = userRepository.findUserByEmail(userEmail);

		if (user == null) {
			return new ResponseDto(false, "User not authenticated", null);
		}

		List<Long> productIds = request.getProductId();

		if (productIds == null || productIds.isEmpty()) {
			return new ResponseDto(false, "Product IDs are required", null);
		}

		List<Wishlist> wishlistItems = new ArrayList<>();

		List<Long> addedProductIds = new ArrayList<>();
		List<Long> alreadyAddedProductIds = new ArrayList<>();
		List<Long> invalidProductIds = new ArrayList<>();

		for (Long productId : productIds) {

			if (productId == null) {
				continue;
			}

			Product product = productRepository.findById(productId).orElse(null);

			if (product == null) {
				invalidProductIds.add(productId);
				continue;
			}

			if (wishlistRepository.existsByUserIdAndProductId(user.getId(), productId)) {
				alreadyAddedProductIds.add(productId);
				continue;
			}

			Wishlist wishlist = new Wishlist();
			wishlist.setUserId(user.getId());
			wishlist.setProductId(product.getId());
			wishlist.setCreatedAt(LocalDateTime.now());

			wishlistItems.add(wishlist);
			addedProductIds.add(productId);
		}

		if (!wishlistItems.isEmpty()) {
			wishlistRepository.saveAll(wishlistItems);
		}

		Map<String, Object> response = new HashMap<>();
		response.put("addedProductIds", addedProductIds);
		response.put("alreadyAddedProductIds", alreadyAddedProductIds);
		response.put("invalidProductIds", invalidProductIds);

		if (!addedProductIds.isEmpty()) {
			return new ResponseDto(true, "Products added to wishlist successfully", response);
		}

		return new ResponseDto(false, "No new products added to wishlist", response);
	}

	public ResponseDto getWishlist(String userEmail) {

		Users user = userRepository.findUserByEmail(userEmail);

		if (user == null) {
			return new ResponseDto(false, "User not authenticated", null);
		}

		List<Wishlist> wishlistItems = wishlistRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

		List<WishlistResponse> responseList = new ArrayList<>();

		for (Wishlist wishlist : wishlistItems) {

			Product product = productRepository.findById(wishlist.getProductId()).orElse(null);

			if (product == null) {
				continue;
			}

			// Fetch product variants
			List<ProductVariant> variants = product.getVariants();

			ProductVariant primaryVariant = variants.isEmpty() ? null : variants.get(0);

			Double price = primaryVariant != null ? primaryVariant.getPrice() : 0.0;

			Double offerPrice = primaryVariant != null ? primaryVariant.getOfferPrice() : price;

			Double discount = price != null && price > 0 ? ((price - offerPrice) / price) * 100 : 0.0;
			
			List<SizeDto> sizes = variants.stream().map(productVariant -> {

				Integer availableStock = productVariant.getStockQuantity();

				return SizeDto.builder().variantId(productVariant.getId()).size(productVariant.getSize())
						.availableStock(availableStock).inStock(availableStock != null && availableStock > 0).build();
			}).collect(Collectors.toList());

			// Fetch product images through variant relationship			
			List<ProductImage> images = product.getImages();

			String imageUrl = images.stream().filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
					.map(ProductImage::getImageUrl).findFirst()
					.orElse(images.isEmpty() ? null : images.get(0).getImageUrl());

			WishlistResponse response = WishlistResponse.builder().wishlistItemId(wishlist.getId())
					.productId(product.getId()).productName(product.getName())
					.brand(product.getBrand() != null ? product.getBrand().getName() : null)
					.imageUrl(imageUrl).sizes(sizes)
					.price(price).offerPrice(offerPrice).discount(Math.round(discount * 100.0) / 100.0)
					.build();

			responseList.add(response);
		}

		return new ResponseDto(true, "Wishlist fetched successfully", responseList);
	}

	@Transactional
	public ResponseDto removeFromWishlist(String userEmail, Long productId) {

		Users user = userRepository.findUserByEmail(userEmail);

		if (user == null) {
			return new ResponseDto(false, "User not authenticated", null);
		}

		Optional<Wishlist> wishlist = wishlistRepository.findByUserIdAndProductId(user.getId(), productId);

		if (wishlist.isEmpty()) {
			return new ResponseDto(false, "Product not found in wishlist", null);
		}

		wishlistRepository.delete(wishlist.get());

		return new ResponseDto(true, "Product removed from wishlist successfully", null);
	}

}
