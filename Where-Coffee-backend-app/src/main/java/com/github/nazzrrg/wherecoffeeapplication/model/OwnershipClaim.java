package com.github.nazzrrg.wherecoffeeapplication.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "ownership_claims")
public class OwnershipClaim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafeteria_id")
    private Cafe cafe;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @JoinColumn(name = "messenger_login")
    private String messengerLogin;

    public OwnershipClaim() {
    }

    public OwnershipClaim(Cafe cafe, User user, String messengerLogin) {
        this.cafe = cafe;
        this.user = user;
        this.messengerLogin = messengerLogin;
    }
}
