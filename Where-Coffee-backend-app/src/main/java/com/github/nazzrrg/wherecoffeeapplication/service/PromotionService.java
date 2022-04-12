package com.github.nazzrrg.wherecoffeeapplication.service;

import com.github.nazzrrg.wherecoffeeapplication.model.Cafe;
import com.github.nazzrrg.wherecoffeeapplication.model.Promotion;
import com.github.nazzrrg.wherecoffeeapplication.model.User;
import com.github.nazzrrg.wherecoffeeapplication.payload.request.PromotionRequest;
import com.github.nazzrrg.wherecoffeeapplication.payload.response.MessageResponse;
import com.github.nazzrrg.wherecoffeeapplication.repo.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class PromotionService {
    @Value("${netcracker.app.itemsOnPage}")
    private int itemsOnPage;
    private final CafeRepository repository;
    private final PromotionRepository promotionRepository;

    public PromotionService(CafeRepository repository,
                       PromotionRepository promotionRepository) {
        this.repository = repository;
        this.promotionRepository = promotionRepository;
    }
    public Promotion getPromotion(long id) {
        return promotionRepository.findById(id).orElseThrow(RuntimeException::new);
    }
    public Page<Promotion> getPage(User user, int page) {
        Pageable pageable = PageRequest.of(page, itemsOnPage);
        Page<Promotion> promotions = promotionRepository.findAllByUser(user, pageable);
        return promotions;
    }
    @Transactional
    public ResponseEntity<MessageResponse> createPromotion(User user, PromotionRequest promoReq) {
        Promotion promo = new Promotion(promoReq.getTitle(),
                promoReq.getDescription(),
                promoReq.getFrom(),
                promoReq.getTo(),
                user);
        Promotion promoEntity = promotionRepository.save(promo);
        for (long id: promoReq.getCafeteriaIds()) {
            Cafe cafe = repository.findById(id).orElseThrow();
            if (cafe.getManager() != null && cafe.getManager().getId() == user.getId()) {
                cafe.getPromotions().add(promoEntity);
                repository.save(cafe);
            }
            else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse(
                                "Error: you are not a moderator of the coffee shop with id " + id + "!"));
            }
        }
        return ResponseEntity
                .ok()
                .body(new MessageResponse("Promotion successfully created!"));
    }
    @Transactional
    public ResponseEntity<MessageResponse> updatePromotion(Long promoId, Long userId, PromotionRequest promoReq) {
        Promotion promo = promotionRepository.getById(promoId);
        if (promo.getUser().getId() == userId) {
            promotionRepository.deletePromotionLinksByPromoId(promoId);
            promo.setTitle(promoReq.getTitle());
            promo.setDescription(promoReq.getDescription());
            promo.setFromDate(promoReq.getFrom());
            promo.setToDate(promoReq.getTo());
            for (long id: promoReq.getCafeteriaIds()) {
                Cafe cafe = repository.findById(id).orElseThrow();
                if (cafe.getManager() != null && cafe.getManager().getId() == userId) {
                    cafe.getPromotions().add(promo);
                    repository.save(cafe);
                }
                else {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse(
                                    "Error: you are not a moderator of the coffee shop with id " + id + "!"));
                }
            }
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("Promotion successfully updated!"));
        }
        else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: сan't edit someone else's promotion!"));
        }
    }
    @Transactional
    public ResponseEntity<MessageResponse> deletePromotion(Long promoId, Long userId) {
        Promotion promo = promotionRepository.getById(promoId);
        if (promo.getUser().getId() == userId) {
            promotionRepository.deletePromotionLinksByPromoId(promoId);
            promotionRepository.delete(promo);
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("Promotion successfully deleted!"));
        }
        else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: сan't delete someone else's promotion!"));
        }
    }
}
