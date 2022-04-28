package com.github.nazzrrg.wherecoffeeapplication.controller;

import com.github.nazzrrg.wherecoffeeapplication.model.Promotion;
import com.github.nazzrrg.wherecoffeeapplication.model.User;
import com.github.nazzrrg.wherecoffeeapplication.payload.request.PromotionRequest;
import com.github.nazzrrg.wherecoffeeapplication.payload.response.MessageResponse;
import com.github.nazzrrg.wherecoffeeapplication.security.services.UserDetailsImpl;
import com.github.nazzrrg.wherecoffeeapplication.service.PromotionService;
import com.github.nazzrrg.wherecoffeeapplication.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "${netcracker.front.api}", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/promotions")
public class PromotionController {
    private final UserService userService;
    private final PromotionService service;

    public PromotionController(UserService userService, PromotionService service) {
        this.userService = userService;
        this.service = service;
    }
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Promotion getPromotion(@PathVariable long id) {
        return service.getPromotion(id);
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_MODERATOR', 'ROLE_ADMIN')")
    public Page<Promotion> getPromotions(Authentication auth,
                                         @RequestParam(name = "page") int page,
                                         @RequestParam(name = "items_on_page", defaultValue = "${netcracker.app.itemsOnPage}") int itemsOnPage) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userService.getById(userDetails.getId());
        Page<Promotion> promotions = service.getPage(user, page, itemsOnPage);
        return promotions;
    }
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> createPromotion(Authentication auth,
                                                           @RequestBody PromotionRequest promoReq) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userService.getById(userDetails.getId());
        return service.createPromotion(user, promoReq);
    }
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> updatePromotion(Authentication auth,
                                @PathVariable long id,
                                @RequestBody PromotionRequest promoReq) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return service.updatePromotion(id, userDetails, promoReq);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> deletePromotion(Authentication auth,
                                @PathVariable long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return service.deletePromotion(id, userDetails);
    }
}
