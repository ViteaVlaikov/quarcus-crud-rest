package com.example.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Cacheable
@Getter
@Setter
public class Fruit extends PanacheEntity {

    @Column(length = 40, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "basket_id")
    private Basket basket;
}