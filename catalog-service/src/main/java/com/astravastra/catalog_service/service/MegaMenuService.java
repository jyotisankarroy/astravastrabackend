package com.astravastra.catalog_service.service;

import org.springframework.stereotype.Service;

import com.astravastra.catalog_service.dto.CategoryMenuResponse;
import com.astravastra.catalog_service.dto.DepartmentMenuResponse;
import com.astravastra.catalog_service.dto.ResponseDto;
import com.astravastra.catalog_service.entity.Category;
import com.astravastra.catalog_service.entity.Department;
import com.astravastra.catalog_service.repository.CategoryRepository;
import com.astravastra.catalog_service.repository.DepartmentRepository;

import java.util.*;

@Service
public class MegaMenuService {
	
	private final CategoryRepository categoryRepository;
	
	private final DepartmentRepository departmentRepository;

    public MegaMenuService(CategoryRepository categoryRepository, DepartmentRepository departmentRepository) {
        this.categoryRepository = categoryRepository;
		this.departmentRepository = departmentRepository;
    }

	public ResponseDto getNavigationMenu() {
		
		ResponseDto response = new ResponseDto();
		List<Department> allDept = departmentRepository.findAll();
		
		List<DepartmentMenuResponse> menuResponse = new ArrayList<>();
		
		for (Department department : allDept) {

	        DepartmentMenuResponse deptResponse = new DepartmentMenuResponse();

	        deptResponse.setId(department.getId());
	        deptResponse.setLabel(department.getName().toUpperCase());
	        deptResponse.setPath(department.getSlug());
	        deptResponse.setDisplayOrder(department.getSort_order());

	        List<CategoryMenuResponse> categoryResponses = new ArrayList<>();

	        List<Category> allCategories =
	                categoryRepository.findAllCtegoriesByDeptId(department.getId());

	        for (Category category : allCategories) {

	            CategoryMenuResponse categoryResponse = buildCategoryResponse(category);

	            categoryResponses.add(categoryResponse);
	        }

	        deptResponse.setSubCategory(categoryResponses);

	        menuResponse.add(deptResponse);
	    }

		response.setStatus(true);
		response.setData(menuResponse);
	    return response;
    }
	
	private CategoryMenuResponse buildCategoryResponse(Category category) {

	    CategoryMenuResponse response = new CategoryMenuResponse();

	    response.setId(category.getId());
	    response.setLabel(category.getMenuName());
	    response.setPath(category.getSlug());
	    response.setDisplayOrder(category.getSortOrder());

	    List<CategoryMenuResponse> subCategoryResponses =
	            new ArrayList<>();

	    List<Category> allSubCategories =
	            categoryRepository.findAllSubCtegoriesByParentId(category.getId());

	    for (Category subCategory : allSubCategories) {

	        CategoryMenuResponse subResponse =
	                buildCategoryResponse(subCategory);

	        subCategoryResponses.add(subResponse);
	    }

	    response.setSubCategory(subCategoryResponses);

	    return response;
	}
    
}
