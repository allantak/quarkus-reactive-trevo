package br.com.jacto.trevo;

import br.com.jacto.trevo.dto.product.ProductDetailDto;
import br.com.jacto.trevo.dto.product.ProductDto;
import br.com.jacto.trevo.model.Culture;
import br.com.jacto.trevo.repository.ProductRepository;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import br.com.jacto.trevo.model.Product;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Path("/product")
@WithSession
public class ProductResource {

    private final Logger logger = Logger.getLogger(Culture.class);

    @Inject
    ProductRepository productRepository;

    @Incoming("products-in")
    public void receive(Record<UUID, String> record) {
        System.out.println(record.key());
        System.out.println(record.value());
    }

    @GET
    public Uni<List<ProductDto>> get() {
        return productRepository.listAll().onItem().transform(products -> products.stream().map(ProductDto::new).toList());
    }

    @GET
    @Path("/{productName}")
    public Uni<Response> getProductName(String productName) {
        return productRepository.findByName(productName)
                .onItem().ifNotNull().transform(product -> {
                    ProductDetailDto productDto = new ProductDetailDto(product);
                    return Response.ok(productDto).build();
                })
                .onItem().ifNull().continueWith(() -> Response.status(Response.Status.NOT_FOUND).build());
    }


    @POST
    public Uni<Response> create(Product product) {
        return Panache.<Product>withTransaction(product::persist)
                .onItem().transform(inserted -> Response.created(URI.create("/product/" + inserted.getProductName())).build());
    }

    @DELETE
    @Path("/{productName}")
    public Uni<Response> delete(String productName) {
        return productRepository.findByName(productName).onItem().ifNotNull().transformToUni(product ->
                productRepository.deleteByName(product.getProductName()).replaceWith(Response.noContent().build())
                ).onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
    }

}
