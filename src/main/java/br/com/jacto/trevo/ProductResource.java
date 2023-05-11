package br.com.jacto.trevo;

import br.com.jacto.trevo.repository.ProductRepository;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import br.com.jacto.trevo.model.Product;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Path("/product")
@WithSession
public class ProductResource {

    @Inject
    ProductRepository productRepository;

    @GET
    public Uni<List<Product>> get() {
        return productRepository.listAll();
    }

    @GET
    @Path("/{productName}")
    public Uni<Product> getProductName(String productName){
        return Product.findByName(productName);
    }

    @POST
    public Uni<Response> create(Product product) {
        return Panache.<Product>withTransaction(product::persist)
                .onItem().transform(inserted -> Response.created(URI.create("/product/" + inserted.getProductId())).build());
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> delete(UUID id) {
        return productRepository.deleteById(id).onItem().transform(delete -> delete ? Response.noContent().build() :
                Response.status(Response.Status.NOT_FOUND).build());
    }
}
