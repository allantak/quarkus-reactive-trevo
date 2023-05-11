package br.com.jacto.trevo;

import br.com.jacto.trevo.config.ConfigResult;
import br.com.jacto.trevo.dto.orderItem.OrderCreateForm;
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
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

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
    public Uni<List<OrderItem>> get() {
        return orderItemRepository.listAll();
    }

    @GET
    @Path("/{email}")
    public Uni<OrderItem> getId(String email){
        return OrderItem.findByEmail(email);
    }

    @POST
    public Uni<Response> create( OrderItem order) {

        Uni<Product> uni = productRepository.findByName(order.getClientName());



        System.out.println("asdasdasdasdasdasd");

        uni.subscribe().with(product -> {
            System.out.println("asdasdasdasdasdasd");
            System.out.println("asdasdasdasdasdasd");
            System.out.println(product.getProductId());

            OrderItem orderSave = new OrderItem(order.getClientName(), order.getClientName(), order.getPhone(),product );

            System.out.println(orderSave.getClientName());

            Panache.<OrderItem>withTransaction(orderSave::persist)
                    .onItem().transform(inserted -> Response.created(URI.create("/order/" + inserted.getOrderItemId())).build());

            System.out.println(orderSave.getClientName());

            // Manipule o objeto Product retornado aqui
        }, failure -> {
            System.out.println("asdasdasdasdasdasd123123");
            System.out.println("asdasdasdasdasdasd123123");
            System.out.println("asdasdasdasdasdasd13123");
            // Manipule a exceção em caso de falha
        });
        return  Panache.<OrderItem>withTransaction(order::persist)
                .onItem().transform(inserted -> Response.created(URI.create("/order/" + inserted.getOrderItemId())).build());
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> delete(UUID id) {
        return orderItemRepository.deleteById(id).onItem().transform(delete -> delete ? Response.noContent().build() :
                Response.status(Response.Status.NOT_FOUND).build());
    }

}
