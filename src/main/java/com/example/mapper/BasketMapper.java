package com.example.mapper;

import com.example.DTO.BasketDTO;
import com.example.DTO.FruitDTO;
import com.example.entity.Basket;
import com.example.entity.Fruit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class BasketMapper {
    @Inject
    FruitMapper fruitMapper;
    public BasketDTO toDTO(Basket basket){
        BasketDTO basketDTO = new BasketDTO();
        basketDTO.setId(basket.getId());
        basketDTO.setName(basket.getName());
        basketDTO.setFruitDTOS(basket.getFruits().stream().map(fruitMapper::toDTO).toList());
        return basketDTO;
    }
    public Basket toEntity(BasketDTO fruitDTO){
        Fruit fruit = new Fruit();
        fruit.setId(fruitDTO.getId());
        fruit.setName(fruitDTO.getName());
        return fruit;
    }
}
