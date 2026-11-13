package com.astravastra.cart_service.service;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.astravastra.cart_service.dto.AddToCartResponse;
import com.astravastra.cart_service.dto.CartItemResponse;
import com.astravastra.cart_service.dto.CartPriceDetails;
import com.astravastra.cart_service.dto.CartResponse;
import com.astravastra.cart_service.dto.CartSelectionRequest;
import com.astravastra.cart_service.dto.RemoveCartRequest;
import com.astravastra.cart_service.dto.ResponseDto;
import com.astravastra.cart_service.dto.SizeDto;
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
	public ResponseDto addToCart(String userEmail, Long productId, Long variantId, Integer quantity) {

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
		CartItem item = cartItemRepository.findByUserIdAndVariantIdAndProductId(user.getId(), variantId, productId).orElse(null);

		int updatedQuantity = quantity;

		if (item != null) {
//			updatedQuantity = item.getQuantity() + quantity;
			return new ResponseDto(false, "This size of the product is already in your cart", null);
		} else {
			item = new CartItem();
			item.setUserId(user.getId());
			item.setProductId(productId);
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
				.productName(variant.getProduct().getName()).size(variant.getSize()).color(variant.getProduct().getColour())
				.quantity(savedItem.getQuantity()).price(variant.getPrice()).offerPrice(variant.getOfferPrice()).build();

		return new ResponseDto(true, "Product added to cart successfully", data);
	}

	@Transactional
	public ResponseDto updateCartSelection(String userEmail, List<CartSelectionRequest> request) {

		Users users = userRepository.findUserByEmail(userEmail);

		if (users == null) {
			return new ResponseDto(false, "User not authenticated", null);
		}

		if (!request.isEmpty()) {

			// Validate all items before updating any of them
			List<CartItem> cartItemsToUpdate = new ArrayList<>();

			for (CartSelectionRequest itemRequest : request) {

				if (itemRequest.getCartItemId() == null || itemRequest.getSelected() == null) {
					return new ResponseDto(false, "Cart item ID and selected status are required", null);
				}

				Optional<CartItem> optionalItem = cartItemRepository.findByIdAndUserId(itemRequest.getCartItemId(),
						users.getId());

				if (optionalItem.isEmpty()) {
					return new ResponseDto(false, "Cart item not found: " + itemRequest.getCartItemId(), null);
				}

				CartItem cartItem = optionalItem.get();

				// Update selection when selected is provided
				if (itemRequest.getSelected() != null) {
					cartItem.setSelected(itemRequest.getSelected());
				}

				// Change size when variantId is provided
				if (itemRequest.getVariantId() != null) {

					ProductVariant newVariant = variantRepository.findById(itemRequest.getVariantId()).orElse(null);

					if (newVariant == null) {
						return new ResponseDto(false, "Selected size not found", null);
					}

					ProductVariant currentVariant = variantRepository.findById(cartItem.getVariantId()).orElse(null);

					if (currentVariant == null) {
						return new ResponseDto(false, "Current product variant not found", null);
					}

					// Validate stock
					if (newVariant.getStockQuantity() == null
							|| newVariant.getStockQuantity() < cartItem.getQuantity()) {
						return new ResponseDto(false, "Selected size is out of stock", null);
					}

					// Check duplicate variant in the same user's cart
//					Optional<CartItem> duplicate = cartItemRepository.findByUserIdAndVariantId(users.getId(),
//							newVariant.getId());
//
//					if (duplicate.isPresent() && !duplicate.get().getId().equals(cartItem.getId())) {
//						return new ResponseDto(false, "This size is already available in your cart", null);
//					}

					cartItem.setVariantId(newVariant.getId());
				}

				cartItemsToUpdate.add(cartItem);
			}

			cartItemRepository.saveAll(cartItemsToUpdate);

		}

		// Return refreshed cart and recalculated price details
		CartResponse cartResponse = getCart(users.getId());

		return new ResponseDto(true, "Cart selection updated successfully", cartResponse);
	}

	public CartResponse getCart(Long userId) {

		List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

		List<CartItemResponse> items = new ArrayList<>();

		int selectedItems = 0;

		double totalMrp = 0.0;
		double discountOnMrp = 0.0;
		double subtotal = 0.0;

		for (CartItem cartItem : cartItems) {

			Long variantId = cartItem.getVariantId();

			ProductVariant variant = variantRepository.findById(variantId).get();

			if (variant == null || variant.getProduct() == null) {
				continue;
			}

			Product product = variant.getProduct();

			Integer quantity = cartItem.getQuantity();

			if (quantity == null || quantity <= 0) {
				continue;
			}

			Double price = variant.getPrice();

			Double offerPrice = variant.getOfferPrice();

			Integer stock = variant.getStockQuantity();

			boolean inStock = stock != null && stock > 0;

			boolean selected = Boolean.TRUE.equals(cartItem.getSelected());

			// Calculate unit prices
			double mrp = price != null ? price : 0.0;

			double sellingPrice = offerPrice != null ? offerPrice : mrp;

			double unitDiscount = Math.max(mrp - sellingPrice, 0.0);
			
			// Fetch all available sizes
			List<ProductVariant> productVariants = variantRepository.findByProductId(product.getId());

			List<SizeDto> sizes = productVariants.stream().map(productVariant -> {

				Integer availableStock = productVariant.getStockQuantity();

				return SizeDto.builder().variantId(productVariant.getId()).size(productVariant.getSize())
						.availableStock(availableStock).selectedSize(productVariant.getId().equals(variantId))
						.inStock(availableStock != null && availableStock > 0).build();
			}).collect(Collectors.toList());

			// Calculate image
			List<ProductImage> images = productImageRepository.findByProduct_IdOrderByDisplayOrderAsc(product.getId());

			String imageUrl = null;

			if (images != null && !images.isEmpty()) {

				ProductImage primaryImage = images.stream().filter(image -> Boolean.TRUE.equals(image.getIsPrimary()))
						.findFirst().orElse(images.get(0));

				imageUrl = primaryImage.getImageUrl();
			}

			// Calculate discount percentage
			double discountPercentage = 0.0;

			if (mrp > 0) {
				discountPercentage = (unitDiscount / mrp) * 100.0;
			}

			// Only selected items contribute to price
			if (selected) {

				selectedItems++;

				if (inStock) {

					totalMrp += mrp * quantity;

					discountOnMrp += unitDiscount * quantity;

					subtotal += sellingPrice * quantity;
				}
			}
			
			CartItemResponse item = CartItemResponse.builder().cartItemId(cartItem.getId()).productId(product.getId())
					.productName(product.getName()).brand(product.getBrand().getName()).sizes(sizes)
					.color(variant.getProduct().getColour()).imageUrl(imageUrl).quantity(quantity).price(price)
					.offerPrice(offerPrice).discount(round(discountPercentage)).selected(selected).build();

			items.add(item);
		}

		// Coupon discount
		double couponDiscount = calculateCouponDiscount(subtotal);

		// Shipping charge
		double shippingCharge = calculateShippingCharge(subtotal);

		// Tax fee
		double tax = calculateTax(subtotal);

		// Price details
		CartPriceDetails priceDetails = CartPriceDetails.builder().totalPrice(round(totalMrp))
				.discount(round(discountOnMrp)).couponDiscount(round(couponDiscount)).tax(round(tax))
				.shippingCharge(round(shippingCharge)).build();

		CartResponse data = CartResponse.builder().items(items).totalItems(items.size()).selectedItems(selectedItems)
				.subtotal(round(subtotal)).priceDetails(priceDetails).build();

		return data;
	}

	private double calculateCouponDiscount(double subtotal) {

		// Replace with actual coupon validation and calculation.
		return 0.0;
	}

	private double calculateShippingCharge(double subtotal) {

		// Replace with your shipping policy.
		return 0.0;
	}

	private double calculateTax(double subtotal) {

		// Replace with your tax policy.
		return 0.0;
	}

	private double round(double amount) {

		return Math.round(amount * 100.0) / 100.0;
	}

	@Transactional
	public ResponseDto removeCartItems(String userEmail, RemoveCartRequest request) {

		Users users = userRepository.findUserByEmail(userEmail);

		if (users == null) {
			return new ResponseDto(false, "User not authenticated", null);
		}

		for (Long cartId : request.getCartItemId()) {

			Optional<CartItem> cartItem = cartItemRepository.findByIdAndUserId(cartId, users.getId());

			if (cartItem.isEmpty()) {
				return new ResponseDto(false, "Cart item not found", null);
			}
			
			cartItemRepository.deleteByIdAndUserId(cartId, users.getId());
		}

		return new ResponseDto(true, "Products removed from cart successfully", null);
	}
	
}
