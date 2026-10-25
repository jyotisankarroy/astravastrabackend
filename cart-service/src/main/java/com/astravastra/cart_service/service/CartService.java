package com.astravastra.cart_service.service;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.astravastra.cart_service.dto.AddToCartResponse;
import com.astravastra.cart_service.dto.CartItemResponse;
import com.astravastra.cart_service.dto.CartResponse;
import com.astravastra.cart_service.dto.ResponseDto;
import com.astravastra.cart_service.entity.CartItem;
import com.astravastra.cart_service.entity.Product;
import com.astravastra.cart_service.entity.ProductImage;
import com.astravastra.cart_service.entity.ProductVariant;
import com.astravastra.cart_service.entity.Users;
import com.astravastra.cart_service.repository.CartItemRepository;
import com.astravastra.cart_service.repository.ProductImageRepository;
import com.astravastra.cart_service.repository.ProductVariantRepository;
import com.astravastra.cart_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CartService {

	private final CartItemRepository cartItemRepository;
	private final ProductVariantRepository variantRepository;
	private final UserRepository userRepository;
	private final ProductImageRepository productImageRepository;

	@Transactional
	public ResponseDto addToCart(String userEmail, Long variantId, Integer quantity) {

		Users user = userRepository.findUserByEmail(userEmail);

		// Validate request
		if (user == null) {
			return new ResponseDto(false, "User not authenticated", null);
		}

		if (variantId == null) {
			return new ResponseDto(false, "Variant ID is required", null);
		}

		if (quantity == null || quantity < 1) {
			return new ResponseDto(false, "Quantity must be at least 1", null);
		}

		// Fetch variant
		ProductVariant variant = variantRepository.findById(variantId).orElse(null);

		if (variant == null) {
			return new ResponseDto(false, "Product variant not found", null);
		}

		if (variant.getProduct() == null) {
			return new ResponseDto(false, "Product is not associated with this variant", null);
		}

		// Validate stock
		Integer stock = variant.getStockQuantity();

		if (stock == null || stock < 1) {
			return new ResponseDto(false, "Product is out of stock", null);
		}

		// Find existing cart item for this user and variant
		CartItem item = cartItemRepository.findByUserIdAndVariantId(user.getId(), variantId).orElse(null);

		int updatedQuantity = quantity;

		if (item != null) {
			updatedQuantity = item.getQuantity() + quantity;
		} else {
			item = new CartItem();
			item.setUserId(user.getId());
			item.setProductId(variant.getProduct().getId());
			item.setVariantId(variant.getId());
			item.setSelected(true);
		}

		if (updatedQuantity > stock) {
			return new ResponseDto(false, "Only " + stock + " units are available", null);
		}

		item.setQuantity(updatedQuantity);

		CartItem savedItem = cartItemRepository.save(item);

		// Prepare response data
		AddToCartResponse data = AddToCartResponse.builder().cartItemId(savedItem.getId())
				.productId(variant.getProduct().getId()).variantId(variant.getId())
				.productName(variant.getProduct().getName()).size(variant.getSize()).color(variant.getColor())
				.quantity(savedItem.getQuantity()).price(variant.getPrice()).offerPrice(variant.getOfferPrice())
				.message("Product added to cart successfully").build();

		return new ResponseDto(true, "Product added to cart successfully", data);
	}

	@Transactional(readOnly = true)
	public ResponseDto getCart(String userEmail) {

		Users user = userRepository.findUserByEmail(userEmail);

		// Validate request
		if (user == null) {
			return new ResponseDto(false, "User not authenticated", null);
		}

		List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());

		List<CartItemResponse> items = new ArrayList<>();

		int selectedItems = 0;
		double subtotal = 0.0;

		for (CartItem cartItem : cartItems) {

			Long variantId = cartItem.getVariantId();

			if (variantId == null) {
				continue;
			}

			ProductVariant productVarient = variantRepository.findById(variantId).get();
			Product product = productVarient.getProduct();

			Integer stock = productVarient.getStockQuantity();
			boolean inStock = stock != null && stock > 0;

			// Fetch variant images
			List<ProductImage> images = productImageRepository.findByVariant_IdOrderByDisplayOrderAsc(variantId);

			String imageUrl = null;

			if (images != null && !images.isEmpty()) {

				ProductImage primaryImage = images.stream().filter(image -> Boolean.TRUE.equals(image.getIsPrimary()))
						.findFirst().orElse(images.get(0));

				imageUrl = primaryImage.getImageUrl();
			}

			Double price = productVarient.getPrice();
			Double offerPrice = productVarient.getOfferPrice();

			double effectivePrice = offerPrice != null ? offerPrice : price != null ? price : 0.0;

			Double discountPercentage = 0.0;

			if (price != null && price > 0 && offerPrice != null) {
				discountPercentage = ((price - offerPrice) / price) * 100.0;
			}

			Boolean selected = Boolean.TRUE.equals(cartItem.getSelected());

			if (selected) {
				selectedItems++;

				if (inStock) {
					subtotal += effectivePrice * cartItem.getQuantity();
				}
			}

			CartItemResponse item = CartItemResponse.builder().cartItemId(cartItem.getId()).productId(product.getId())
					.variantId(variantId).productName(product.getName()).slug(product.getSlug())
					.size(productVarient.getSize()).color(productVarient.getColor()).imageUrl(imageUrl)
					.quantity(cartItem.getQuantity()).availableStock(stock).price(price).offerPrice(offerPrice)
					.discountPercentage(discountPercentage).selected(selected).inStock(inStock).build();

			items.add(item);
		}

		CartResponse data = CartResponse.builder().items(items).totalItems(items.size()).selectedItems(selectedItems)
				.subtotal(Math.round(subtotal * 100.0) / 100.0).build();

		return new ResponseDto(true, "Data fetched Successfully", data);
	}

}
