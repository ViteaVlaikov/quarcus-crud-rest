package com.example.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Cacheable
@Getter
@Setter
public class Fruit extends PanacheEntity {

    @Column(length = 40, unique = true)
    public String name;

}