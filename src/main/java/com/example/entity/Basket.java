package com.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@Setter
public class Basket extends PanacheEntityBase {
    private String name;
    @OneToMany(mappedBy = "basket", fetch = FetchType.EAGER)
    private List<Fruit> fruits;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
