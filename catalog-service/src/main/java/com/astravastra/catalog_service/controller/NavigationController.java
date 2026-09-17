package com.astravastra.catalog_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astravastra.catalog_service.dto.ResponseDto;
import com.astravastra.catalog_service.service.MegaMenuService;

@RestController
@RequestMapping("/api/catalog")
public class NavigationController {

    private final MegaMenuService megaMenuService;

    public NavigationController(MegaMenuService megaMenuService) {
        this.megaMenuService = megaMenuService;
    }

    @GetMapping("/menu")
    public ResponseEntity<ResponseDto> getMegaMenu() {
    	
        ResponseDto navigationMenu = megaMenuService.getNavigationMenu();
        
        return ResponseEntity.ok(navigationMenu);
    }
}
