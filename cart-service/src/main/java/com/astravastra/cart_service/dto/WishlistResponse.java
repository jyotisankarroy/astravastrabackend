package com.astravastra.cart_service.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WishlistResponse {

    private Long wishlistItemId;
    private Long productId;

    private String brand;
    private String productName;

    private List<SizeDto> sizes;
    private String imageUrl;

    private Double price;
    private Double offerPrice;
    private Double discount;

}
