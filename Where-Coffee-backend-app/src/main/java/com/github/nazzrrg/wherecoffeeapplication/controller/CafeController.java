package com.github.nazzrrg.wherecoffeeapplication.controller;

import com.github.nazzrrg.wherecoffeeapplication.model.Cafe;
import com.github.nazzrrg.wherecoffeeapplication.model.OwnershipClaim;
import com.github.nazzrrg.wherecoffeeapplication.model.Promotion;
import com.github.nazzrrg.wherecoffeeapplication.model.User;
import com.github.nazzrrg.wherecoffeeapplication.payload.request.CafeRequest;
import com.github.nazzrrg.wherecoffeeapplication.payload.request.GradeRequest;
import com.github.nazzrrg.wherecoffeeapplication.payload.request.OwnershipRequest;
import com.github.nazzrrg.wherecoffeeapplication.payload.request.PromotionRequest;
import com.github.nazzrrg.wherecoffeeapplication.payload.response.MessageResponse;
import com.github.nazzrrg.wherecoffeeapplication.payload.response.OwnershipClaimResponse;
import com.github.nazzrrg.wherecoffeeapplication.security.services.UserDetailsImpl;
import com.github.nazzrrg.wherecoffeeapplication.service.CafeService;
import com.github.nazzrrg.wherecoffeeapplication.service.UserService;
import com.github.nazzrrg.wherecoffeeapplication.service.YandexMapService;
import com.github.nazzrrg.wherecoffeeapplication.utils.DTOMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/cafeterias")
public class CafeController {
    private final CafeService service;
    private final UserService userService;
    private final YandexMapService mapService;

    public CafeController(CafeService service, UserService userService, YandexMapService mapService) {
        this.service = service;
        this.userService = userService;
        this.mapService = mapService;
    }

    @PostMapping
    public void create(@RequestBody CafeRequest cafe) {
        service.create(cafe);
    }

    @GetMapping
    public List<Cafe> getCafePage(@RequestParam(value = "page") Integer page,
                                  @RequestParam(value = "location", defaultValue = "59.965361,30.311645") String location,
                                  @RequestParam(value = "dist", defaultValue = "1.0") Double dist,
                                  @RequestParam(value = "confirmed", defaultValue = "true") boolean confirmed) {
        /** переделать с dto */
        Page<Cafe> cafeterias = service.getPage(page, location, dist, confirmed);
        return cafeterias.getContent();
    }

    @GetMapping("/pages-count")
    public int getCountPage(@RequestParam(value = "location", defaultValue = "59.965361,30.311645") String location,
                            @RequestParam(value = "dist", defaultValue = "1.0") Double dist,
                            @RequestParam(value = "confirmed", defaultValue = "true") boolean confirmed) {
        return service.getPageCount(location, dist, confirmed);
    }

    @GetMapping("/{id}")
    public Cafe getCafe(@PathVariable long id) {
        /** переделать с dto */
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteCafe(@PathVariable long id) {
        service.delete(id);
    }

    @PatchMapping("/{id}")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateCafe(@PathVariable long id,
                           @RequestBody CafeRequest cafe) {
        service.update(id, cafe);
    }

    @PostMapping("/{id}/desire-to-own")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_USER')")
    public void desireToOwn(Authentication auth,
                            @PathVariable long id,
                            @RequestBody OwnershipRequest ownershipRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userService.getById(userDetails.getId());
        service.addDesireToOwn(id, user, ownershipRequest.getMessengerLogin());
    }
    @GetMapping("/ownership-claims")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<OwnershipClaimResponse>> getOwnershipClaims(
            @RequestParam(name = "page") Integer page) {
        return service.getClaimsPage(page);
    }
    @DeleteMapping("/ownership-claims/{id}")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public void rejectOwnership(@PathVariable long id) {
        service.rejectOwnership(id);
    }
    @PostMapping("/ownership-claims/{id}")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public void confirmOwnership(@PathVariable long id) {
        long userId = service.confirmOwnership(id);
        userService.giveModeratorRights(userId);
    }
    @PostMapping("/{id}/confirm")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public void confirmCafe(@PathVariable long id) {
        service.confirmCafe(id);
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<MessageResponse> addReview(Authentication auth,
                                                     @PathVariable long id,
                                                     @RequestBody GradeRequest grade) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userService.getById(userDetails.getId());
        return service.addReview(id, user, grade);
    }
    @PatchMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> updateReview(Authentication auth,
                                                        @PathVariable long id,
                                                        @RequestBody GradeRequest grade) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return service.updateReview(id, userDetails.getId(), grade);
    }
    @DeleteMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> deleteReview(Authentication auth,
                                                        @PathVariable long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return service.deleteReview(id, userDetails.getId());
    }

    @PostMapping("/update")
    public ResponseEntity<MessageResponse> updateCafeterias(@RequestParam(value = "res",defaultValue = "1") Integer res,
                                                            @Value("${netcracker.app.areas}") String areas,
                                                            @Value("${netcracker.app.updateURL}") String urlPattern,
                                                            @Value("${netcracker.app.mapAPI}") String apikey) {
        return mapService.updateCafeterias(res, areas, urlPattern, apikey);
    }
}
