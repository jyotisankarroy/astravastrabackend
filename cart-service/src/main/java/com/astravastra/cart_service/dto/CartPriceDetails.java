package com.astravastra.cart_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartPriceDetails {

    private Double totalPrice;

    private Double discount;

    private Double couponDiscount;

    private Double tax;

    private Double shippingCharge;

}
