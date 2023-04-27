package com.example.resource;

import com.example.DTO.FruitDTO;
import com.example.entity.Fruit;
import com.example.mapper.FruitMapper;
import com.example.repository.FruitRepository;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.example.codes.ErrorCode.UNPROCESSABLE_ENTITY_CODE;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

@Path("fruits")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class FruitResource {
    @Inject
    FruitRepository fruitRepository;
    @Inject
    FruitMapper fruitMapper;

    @GET
    public Uni<List<FruitDTO>> get() {
        return fruitRepository.findAll().list()
                .map(fruits -> fruits.stream()
                        .map(fruitMapper::toDTO)
                        .toList());
    }

    @GET
    @Path("{id}")
    public Uni<FruitDTO> getById(@PathParam("id") Long id) {
        return fruitRepository.findById(id)
                .map(fruitMapper::toDTO);
    }

    @POST
    @Transactional
    public Uni<Response> create(FruitDTO fruitDTO) {
        if (fruitDTO == null || fruitDTO.getId() != null) {
            throw new WebApplicationException("Invalid data in request.", UNPROCESSABLE_ENTITY_CODE);
        }
        Fruit fruit = fruitMapper.toEntity(fruitDTO);
        return fruitRepository.persist(fruit)
                .flatMap(fruit1 -> fruitRepository.flush().replaceWith(fruit1))
                .replaceWith(Response.ok(fruit).status(CREATED)::build);
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Uni<Response> update(@PathParam("id") Long id, FruitDTO fruitDTO) {
        if (fruitDTO == null || fruitDTO.getName() == null) {
            throw new WebApplicationException("Fruit name was not set on request.", UNPROCESSABLE_ENTITY_CODE);
        }
        Fruit fruit = fruitMapper.toEntity(fruitDTO);
        return fruitRepository.findById(id)
                .onItem().ifNotNull().invoke(entity -> entity.setName(fruit.getName()))
                .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
                .onItem().ifNull().continueWith(Response.ok().status(NOT_FOUND)::build);
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Uni<Response> delete(@PathParam("id") Long id) {
        return fruitRepository.deleteById(id)
                .map(deleted -> deleted
                        ? Response.ok().status(NO_CONTENT).build()
                        : Response.ok().status(NOT_FOUND).build());
    }
}
