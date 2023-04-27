package com.example.resource;

import com.example.DTO.BasketDTO;
import com.example.DTO.FruitDTO;
import com.example.entity.Basket;
import com.example.entity.Fruit;
import com.example.mapper.BasketMapper;
import com.example.repository.BasketRepository;
import com.example.repository.FruitRepository;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

import static com.example.codes.ErrorCode.UNPROCESSABLE_ENTITY_CODE;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.NO_CONTENT;

@Path("baskets")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class BasketResource {
    @Inject
    BasketRepository basketRepository;
    @Inject
    FruitRepository fruitRepository;
    @Inject
    BasketMapper basketMapper;


    @GET
    public Uni<List<BasketDTO>> getAll() {
        return basketRepository.findAll().list()
                .map(baskets -> baskets.stream()
                        .map(basketMapper::toDTO)
                        .toList());
    }

    @GET
    @Path("{id}")
    public Uni<BasketDTO> getById(@PathParam("id") Long id) {
        return basketRepository.findById(id)
                .map(basketMapper::toDTO);
    }

    @GET
    @Path("/by_fruit_name")
    public Uni<List<BasketDTO>> getAllByFruitName(@QueryParam("fruit_name") String fruitName){
        return basketRepository.findAll().list()
                .map(baskets -> baskets.stream()
                        .map(basketMapper::toDTO)
                        .filter(
                                basketDTO -> basketDTO.getFruitDTOS().stream()
                                        .map(FruitDTO::getName)
                                        .filter(name->name.equals(fruitName))
                                        .toList()
                                        .size() != 0
                                )
                                .toList()
                        );
    }

    @POST
    @Transactional
    public Response create(BasketDTO basketDTO) {
        if (basketDTO.getId() != null) {
            throw new WebApplicationException("Invalid data in request.", UNPROCESSABLE_ENTITY_CODE);
        }
        Basket basket = basketMapper.toEntity(basketDTO);
//        return basketRepository.persist(basket)
//                .flatMap(basket1 -> basketRepository.flush().replaceWith(basket1))
//                .replaceWith(Response.ok(basket).status(CREATED)::build);
        basketRepository.persist(basket);
        basketRepository.flush();
        if(basketRepository.isPersistent(basket)){
            return Response.created(URI.create("/baskets/"+basket.getId())).build();
        }else {
            return Response.status(NOT_FOUND).build();
        }
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(@PathParam("id") Long id, BasketDTO basketDTO) {
        if (basketDTO == null) {
            throw new WebApplicationException("Basket was not set on request.", UNPROCESSABLE_ENTITY_CODE);
        }
        Basket basket = basketMapper.toEntity(basketDTO);
        return basketRepository.findById(id)
                .onItem().ifNotNull().invoke(entity -> entity.setFruits(basket.getFruits()))
                .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
                .onItem().ifNull().continueWith(Response.ok().status(NOT_FOUND)::build);
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(@PathParam("id") Long id) {
        return Panache.withTransaction(() -> Basket.deleteById(id))
                .map(deleted -> deleted
                        ? Response.ok().status(NO_CONTENT).build()
                        : Response.ok().status(NOT_FOUND).build());
    }
}
