package br.com.jacto.trevo;

import br.com.jacto.trevo.dto.OrderDto;
import br.com.jacto.trevo.model.OrderItem;
import br.com.jacto.trevo.repository.OrderItemRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@Path("/order")
@WithSession
public class OrderItemResource {

  @Inject
  OrderItemRepository orderItemRepository;

  @Inject
  @Channel("products-out")
  Emitter<Record<UUID, String>> emitter;

  @GET
  public Uni<List<OrderDto>> get() {
    return orderItemRepository
      .listAll()
      .onItem()
      .transform(orderItems ->
        orderItems.stream().map(OrderDto::new).collect(Collectors.toList())
      );
  }

  @GET
  @Path("/{email}")
  public Uni<Response> getId(String email) {
    return orderItemRepository.findByEmail(email).onItem().ifNotNull().transform(order -> {
      OrderDto orderDto = new OrderDto(order);
      return Response.ok(orderDto).build();
    } ).onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
  }

  @POST
  public Uni<Response> create(OrderItem orderItem) {
    return Panache
      .<OrderItem>withTransaction(orderItem::persist)
      .onItem()
      .transform(inserted ->
        Response
          .created(URI.create("/product/" + inserted.getEmail()))
          .build()
      )
      .onItem()
      .invoke(() -> sendOrderKafka(orderItem));
  }

  @DELETE
  @Path("/{email}")
  public Uni<Response> delete(String email) {
    return orderItemRepository.findByEmail(email).onItem().ifNotNull().transformToUni(product ->
            orderItemRepository.deleteByEmail(product.getEmail()).replaceWith(Response.noContent().build())
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
