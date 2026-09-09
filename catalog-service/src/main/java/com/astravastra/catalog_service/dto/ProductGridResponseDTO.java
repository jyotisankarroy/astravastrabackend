package com.astravastra.catalog_service.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductGridResponseDTO {
	private boolean status;
    private Metadata metadata;
    private Filters filters;
    private List<ProductSummaryDTO> data;

}
