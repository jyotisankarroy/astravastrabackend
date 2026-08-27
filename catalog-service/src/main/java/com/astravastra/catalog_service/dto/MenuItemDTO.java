package com.astravastra.catalog_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import com.astravastra.catalog_service.entity.NavigationMenu;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MenuItemDTO {
    private Long id;
    private String label;
    private Long categoryId;
    private String path;
    private Long displayOrder;
    
    private List<MenuItemDTO> subCategory = new ArrayList<>();

}
