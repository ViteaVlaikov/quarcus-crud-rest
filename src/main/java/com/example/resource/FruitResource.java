package com.example.resource;

import com.example.entity.Fruit;
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

    @GET
    public Uni<List<Fruit>> get() {
        return fruitRepository.findAll().list();
    }

    @GET
    @Path("{id}")
    public Uni<Fruit> getById(@PathParam("id") Long id) {
        return fruitRepository.findById(id);
    }

    @POST
    @Transactional
    public Uni<Response> create(Fruit fruit) {
        if (fruit == null || fruit.getId() != null) {
            throw new WebApplicationException("Id was invalidly set on request.", UNPROCESSABLE_ENTITY_CODE);
        }

        return fruitRepository.persist(fruit)
                .replaceWith(Response.ok(fruit).status(CREATED)::build);
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Uni<Response> update(@PathParam("id") Long id, Fruit fruit) {
        if (fruit == null || fruit.getName() == null) {
            throw new WebApplicationException("Fruit name was not set on request.", UNPROCESSABLE_ENTITY_CODE);
        }

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
