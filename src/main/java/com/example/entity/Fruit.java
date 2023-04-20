package com.example.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class Fruit extends PanacheEntityBase {

    @Column(length = 40, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "basket_id")
    private Basket basket;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}