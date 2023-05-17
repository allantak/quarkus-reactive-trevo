package br.com.jacto.trevo;

import br.com.jacto.trevo.dto.OrderCreateForm;
import br.com.jacto.trevo.dto.OrderDto;
import br.com.jacto.trevo.model.OrderItem;
import br.com.jacto.trevo.repository.OrderItemRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;


@Authenticated
@Path("/order")
@WithSession
public class OrderItemResource {
    @Inject
    OrderItemRepository orderItemRepository;

    @Inject
    @Channel("products-out")
    Emitter<Record<UUID, String>> emitter;

    @Inject
    Validator validator;

    private static final Logger LOG = Logger.getLogger(OrderItemResource.class);

    @GET
    @RolesAllowed("admin")
    public Uni<List<OrderDto>> get() {
        return orderItemRepository
                .listAll()
                .onItem()
                .transform(orderItems ->
                        orderItems.stream().map(OrderDto::new).collect(Collectors.toList())
                ).onItem().invoke(LOG::info);
    }

    @GET
    @Path("/{email}")
    public Uni<Response> getId(String email) {
        return orderItemRepository.findByEmail(email).onItem().ifNotNull().transform(order -> {
            OrderDto orderDto = new OrderDto(order);
            LOG.info(orderDto);
            return Response.ok(orderDto).build();
        }).onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Uni<Response> create(@Valid OrderCreateForm orderItem) {
        Set<ConstraintViolation<OrderCreateForm>> violations = validator.validate(orderItem);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Validation failed", violations);
        }
        OrderItem orderItemSave = new OrderItem(orderItem.getClientName(), orderItem.getEmail(), orderItem.getPhone(), orderItem.getProduct());
        return Panache
                .<OrderItem>withTransaction(orderItemSave::persist)
                .onItem()
                .transform(inserted -> {
                    LOG.info(inserted);
                    return Response.created(URI.create("/product/" + inserted.getEmail())).build();
                })
                .onItem()
                .invoke(() -> sendOrderKafka(orderItemSave));
    }

    @DELETE
    @Path("/{email}")
    public Uni<Response> delete(String email) {
        return orderItemRepository.findByEmail(email).onItem().ifNotNull().transformToUni(product ->
                {
                    LOG.info(product);
                    return orderItemRepository.deleteByEmail(product.getEmail()).replaceWith(Response.noContent().build());
                }
        ).onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
    }

    public void sendOrderKafka(OrderItem orderItem) {
        try {
            String orderString = OrderItem.convertToString(orderItem);
            emitter.send(Record.of(UUID.randomUUID(), orderString));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
