package com.github.nazzrrg.wherecoffeeapplication.payload.response;

import lombok.Data;

@Data
public class OwnershipClaimResponse {
    private Long id;
    private Long cafeId;
    private Long userId;
    private String messengerLogin;

    public OwnershipClaimResponse(Long id, Long cafeId, Long userId, String messengerLogin) {
        this.id = id;
        this.cafeId = cafeId;
        this.userId = userId;
        this.messengerLogin = messengerLogin;
    }
}
