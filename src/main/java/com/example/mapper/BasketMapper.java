package com.example.mapper;

import com.example.DTO.BasketDTO;
import com.example.entity.Basket;

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
    public Basket toEntity(BasketDTO basketDTO){
        Basket basket = new Basket();
        basket.setId(basket.getId());
        basket.setName(basket.getName());
        basket.setFruits(basketDTO.getFruitDTOS().stream().map(fruitMapper::toEntity).toList());
        return basket;
    }
}
