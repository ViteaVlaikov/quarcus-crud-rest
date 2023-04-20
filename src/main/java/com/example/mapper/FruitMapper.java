package com.example.mapper;

import com.example.DTO.FruitDTO;
import com.example.entity.Fruit;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FruitMapper {
    public FruitDTO toDTO(Fruit fruit){
        FruitDTO fruitDTO = new FruitDTO();
        fruitDTO.setId(fruit.getId());
        fruitDTO.setName(fruit.getName());
        return fruitDTO;
    }
    public Fruit toEntity(FruitDTO fruitDTO){
        Fruit fruit = new Fruit();
        fruit.setId(fruitDTO.getId());
        fruit.setName(fruitDTO.getName());
        return fruit;
    }
}
