package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.RestaurantService;

@Controller
public class HomeController {
    private final RestaurantService restaurantService;
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;
    
    public HomeController(RestaurantService restaurantService, RestaurantRepository restaurantRepository, CategoryRepository categoryRepository) {
        this.restaurantService = restaurantService;
        this.restaurantRepository = restaurantRepository;
        this.categoryRepository = categoryRepository;
    }
    
    @GetMapping("/")    
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {   
        if (userDetailsImpl != null && "ROLE_ADMIN".equals(userDetailsImpl.getUser().getRole().getName())) {
            return "redirect:/admin";
        }
                
        List<Restaurant> restaurants = restaurantRepository.findAll();
        List<Restaurant> sortedRestaurants = restaurantService.sortByRatingDesc(restaurants);
        List<Restaurant> highlyRatedRestaurants = sortedRestaurants.subList(0, Math.min(sortedRestaurants.size(), 6));        
        Page<Restaurant> newRestaurants = restaurantRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 6));
        Category washoku = categoryRepository.findFirstByName("和食");
        Category udon = categoryRepository.findFirstByName("うどん");
        Category don = categoryRepository.findFirstByName("丼物");
        Category ramen = categoryRepository.findFirstByName("ラーメン");
        Category oden = categoryRepository.findFirstByName("おでん");
        Category fried = categoryRepository.findFirstByName("揚げ物");
        List<Category> categories = categoryRepository.findAll();
        
        model.addAttribute("highlyRatedRestaurants", highlyRatedRestaurants);
        model.addAttribute("newRestaurants", newRestaurants);
        model.addAttribute("washoku", washoku);
        model.addAttribute("udon", udon);
        model.addAttribute("don", don);
        model.addAttribute("ramen", ramen);
        model.addAttribute("oden", oden);
        model.addAttribute("fried", fried);
        model.addAttribute("categories", categories);
        
        return "index";
    }   
}