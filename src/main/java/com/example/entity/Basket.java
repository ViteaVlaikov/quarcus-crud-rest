package com.example.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Cacheable
@Getter
@Setter
public class Basket extends PanacheEntity {
    private String name;
    @OneToMany(mappedBy = "basket", fetch = FetchType.EAGER)
    private List<Fruit> fruits;


}
