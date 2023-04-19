package com.example.resource;

import com.example.entity.Basket;
import com.example.entity.Fruit;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import org.springframework.web.bind.annotation.RequestBody;

import javax.enterprise.context.ApplicationScoped;
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

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.NO_CONTENT;

@Path("baskets")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class BasketResource {
    @GET
    public Uni<List<Basket>> get() {
        return Basket.listAll();
    }

    @GET
    @Path("{id}")
    public Uni<Basket> getById(@PathParam("id") Long id) {
        return Basket.findById(id);
    }

    @POST
    public Uni<Response> create(@RequestBody List<Fruit> fruits) {
        for (Fruit fruit : fruits) {
            if (fruit == null || fruit.id != null) {
                throw new WebApplicationException("Id was invalidly set on request.", 422);
            }
            Panache.withTransaction(fruit::persist);
        }
        Basket basket = new Basket();
        basket.setFruits(fruits);
        return Panache.withTransaction(basket::persist)
                .replaceWith(Response.ok(basket).status(CREATED)::build);
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(@PathParam("id") Long id, @RequestBody Basket basket) {
        if (basket == null) {
            throw new WebApplicationException("Fruit name was not set on request.", 422);
        }

        return Panache
                .withTransaction(() -> Basket.<Basket>findById(id)
                        .onItem().ifNotNull().invoke(entity -> entity.setFruits(basket.getFruits()))
                )
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
