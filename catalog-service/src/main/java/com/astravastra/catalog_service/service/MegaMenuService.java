package com.astravastra.catalog_service.service;

import org.springframework.stereotype.Service;

import com.astravastra.catalog_service.dto.MenuItemDTO;
import com.astravastra.catalog_service.entity.NavigationMenu;
import com.astravastra.catalog_service.repository.NavigationMenuRepository;

import java.util.*;

@Service
public class MegaMenuService {

    private final NavigationMenuRepository repository;
    
    public MegaMenuService(NavigationMenuRepository repository) {
        this.repository = repository;
    }

	public Map<String, Object> getMegaMenuTree() {
        // Fetch all rows in one single fast DB query
        List<NavigationMenu> allMenus = repository.findAllByOrderByDisplayOrderAsc();
        

        // Create Maps to hold DTOs and identify the root elements
        Map<Long, MenuItemDTO> dtoMap = new HashMap<>();
        List<MenuItemDTO> rootNodes = new ArrayList<>();

        // Convert all Database Entities to JSON DTOs
        for (NavigationMenu menu : allMenus) {
        	MenuItemDTO dto = new MenuItemDTO();
        	dto.setCategoryId(menu.getCategoryId());
        	dto.setDisplayOrder(menu.getDisplayOrder());
        	dto.setId(menu.getId());
        	dto.setLabel(menu.getLabel());
        	dto.setPath(menu.getSlug());
        	
            dtoMap.put(menu.getId(), dto);
        }

        // Assemble the Tree
        for (NavigationMenu menu : allMenus) {
            MenuItemDTO currentDto = dtoMap.get(menu.getId());

            if (menu.getParentId() == null) {
                // If it has no parent, it's a Root Tab (MEN, WOMEN, KIDS)
                rootNodes.add(currentDto);
            } else {
                // If it has a parent, attach it to the parent's children array
                MenuItemDTO parentDto = dtoMap.get(menu.getParentId());
                if (parentDto != null) {
                    parentDto.getSubCategory().add(currentDto);
                }
            }
        }

        // Wrap in the final response format
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", true);
        response.put("data", rootNodes);
        
        return response;
    }
    
}
