package com.astravastra.catalog_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astravastra.catalog_service.service.MegaMenuService;

import java.util.Map;

@RestController
@RequestMapping("/api/catalog")
public class NavigationController {

    private final MegaMenuService megaMenuService;

    public NavigationController(MegaMenuService megaMenuService) {
        this.megaMenuService = megaMenuService;
    }

    @GetMapping("/menu")
    public ResponseEntity<Map<String, Object>> getMegaMenu() {
    	
        Map<String, Object> response = megaMenuService.getMegaMenuTree();
        
        return ResponseEntity.ok(response);
    }
}
