package com.github.nazzrrg.wherecoffeeapplication.model;

import java.io.Serializable;
import java.util.Objects;

public class CafePerkId implements Serializable {
    private Long cafe;
    private Long perk;

    public CafePerkId() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CafePerkId that = (CafePerkId) o;
        return cafe.equals(that.cafe) && perk.equals(that.perk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cafe, perk);
    }
}
