package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.FavoriteService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class FavoriteController {
    private static final String ROLE_FREE_MEMBER = "ROLE_FREE_MEMBER";
    private static final String SUBSCRIPTION_MESSAGE = "この機能を利用するには有料プランへの登録が必要です。";
    private static final String SUBSCRIPTION_URL = "/subscription/register";    
    
    private final FavoriteRepository favoriteRepository;
    private final RestaurantRepository restaurantRepository; 
    private final UserRepository userRepository;
    private final FavoriteService favoriteService; 
    
    public FavoriteController(FavoriteRepository favoriteRepository, RestaurantRepository restaurantRepository, UserRepository userRepository, FavoriteService favoriteService) {        
        this.favoriteRepository = favoriteRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.favoriteService = favoriteService;
    }
    
    @GetMapping("/favorites")
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                        @PageableDefault(page = 0, size = 15, sort = "id") Pageable pageable,
                        RedirectAttributes redirectAttributes,
                        Model model) {  
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());     
        
        if (ROLE_FREE_MEMBER.equals(user.getRole().getName())) {
            redirectAttributes.addFlashAttribute("subscriptionMessage", SUBSCRIPTION_MESSAGE);
            return "redirect:" + SUBSCRIPTION_URL;
        }
        
        Page<Favorite> favoritePage = favoriteRepository.findByUserOrderByCreatedAtDesc(user, pageable);       
                            
        model.addAttribute("favoritePage", favoritePage);                
        
        return "favorites/index";
    }     
    
    @PostMapping("/restaurants/{restaurantId}/favorites/create")
    public String create(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,  
                         @PathVariable(name = "restaurantId") Integer restaurantId,                                                                         
                         RedirectAttributes redirectAttributes,
                         Model model)
    {    
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());       
        
        if (ROLE_FREE_MEMBER.equals(user.getRole().getName())) {
            redirectAttributes.addFlashAttribute("subscriptionMessage", SUBSCRIPTION_MESSAGE);
            return "redirect:" + SUBSCRIPTION_URL;
        }
        
        Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);        
        
        favoriteService.create(restaurant, user);
        redirectAttributes.addFlashAttribute("successMessage", "お気に入りに追加しました。");    
        
        return "redirect:/restaurants/{restaurantId}";
    }
    
    @PostMapping("/favorites/{favoriteId}/delete")
    public String delete(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,                         
                         @PathVariable(name = "favoriteId") Integer favoriteId,
                         RedirectAttributes redirectAttributes,
                         HttpServletRequest httpServletRequest) 
    {    
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());       
        
        if (ROLE_FREE_MEMBER.equals(user.getRole().getName())) {
            redirectAttributes.addFlashAttribute("subscriptionMessage", SUBSCRIPTION_MESSAGE);
            return "redirect:" + SUBSCRIPTION_URL;
        }                        
        
        Favorite favorite = favoriteRepository.findById(favoriteId).orElse(null);
        
        if (favorite == null || !favorite.getUser().equals(user)) {
            return "redirect:/error";
        }
        
        favoriteService.delete(favorite);
                
        redirectAttributes.addFlashAttribute("successMessage", "お気に入りを解除しました。");
        
        String referer = httpServletRequest.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/restaurants/{restaurantId}");        
    }     
}
