package com.astravastra.catalog_service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListingResponse {
	private boolean status;
    private ProductMetadata metadata;
    private ProcuctFilters filters;
    private List<ProductResponse> data;

}
