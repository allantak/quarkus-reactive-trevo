package br.com.jacto.trevo;

import br.com.jacto.trevo.config.ConfigResult;
import br.com.jacto.trevo.dto.orderItem.OrderCreateForm;
import br.com.jacto.trevo.dto.orderItem.OrderDto;
import br.com.jacto.trevo.model.OrderItem;
import br.com.jacto.trevo.model.Product;
import br.com.jacto.trevo.repository.OrderItemRepository;
import br.com.jacto.trevo.repository.ProductRepository;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Path("/order")
@WithSession
public class OrderItemResource {

    @Inject
    OrderItemRepository orderItemRepository;

    @Inject
    ProductRepository productRepository;

    @Inject
    Validator validator;

    @GET
    public Uni<List<OrderDto>> get() {
        return orderItemRepository.listAll().onItem().transform(orderItems -> orderItems.stream().map(OrderDto::new).collect(Collectors.toList()));
    }

    @GET
    @Path("/{email}")
    public Uni<OrderDto> getId(String email){
        return OrderItem.findByEmail(email).onItem().transform(OrderDto::new);
    }

    @POST
    public Uni<Response> create(OrderCreateForm order) {
        return productRepository.findByName(order.getProductName())
                .onItem().transformToUni(product -> {
                    OrderItem newOrderItem = new OrderItem(order.getClientName(), order.getClientName(), order.getPhone(), product);
                    return Panache.withTransaction(newOrderItem::persist)
                            .replaceWith(Response.created(URI.create("/order/" + newOrderItem.getOrderItemId())).build());
                });
    }


    @DELETE
    @Path("/{id}")
    public Uni<Response> delete(UUID id) {
        return orderItemRepository.deleteById(id).onItem().transform(delete -> delete ? Response.noContent().build() :
                Response.status(Response.Status.NOT_FOUND).build());
    }

}
