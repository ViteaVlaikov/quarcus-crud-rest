package com.example.resource;

import com.example.entity.Basket;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
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

    @GET
    public Uni<List<Basket>> get() {
        return basketRepository.findAll().list();
    }

    @GET
    @Path("{id}")
    public Uni<Basket> getById(@PathParam("id") Long id) {
        return basketRepository.findById(id);
    }

    @POST
    @Transactional
    public Uni<Response> create(Basket basket) {
        return basketRepository.persist(basket)
                .replaceWith(Response.ok(basket).status(CREATED)::build);
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(@PathParam("id") Long id, Basket basket) {
        if (basket == null) {
            throw new WebApplicationException("Basket was not set on request.", UNPROCESSABLE_ENTITY_CODE);
        }

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
