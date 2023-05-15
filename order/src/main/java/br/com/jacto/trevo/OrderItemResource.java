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
  public Uni<OrderDto> getId(String email) {
    return OrderItem.findByEmail(email).onItem().transform(OrderDto::new);
  }

  @POST
  public Uni<Response> create(OrderItem orderItem) {
    return Panache
      .<OrderItem>withTransaction(orderItem::persist)
      .onItem()
      .transform(inserted ->
        Response
          .created(URI.create("/product/" + inserted.getClientName()))
          .build()
      )
      .onItem()
      .invoke(() -> sendMovieToKafka(orderItem));
  }

  @DELETE
  @Path("/{id}")
  public Uni<Response> delete(UUID id) {
    return orderItemRepository
      .deleteById(id)
      .onItem()
      .transform(delete ->
        delete
          ? Response.noContent().build()
          : Response.status(Response.Status.NOT_FOUND).build()
      );
  }

  public void sendMovieToKafka(OrderItem orderItem) {
    try {
      String orderString = OrderItem.convertToString(orderItem);
      emitter.send(Record.of(UUID.randomUUID(), orderString));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
