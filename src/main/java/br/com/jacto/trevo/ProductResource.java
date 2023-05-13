package br.com.jacto.trevo;

import br.com.jacto.trevo.dto.product.ProductDetailDto;
import br.com.jacto.trevo.dto.product.ProductDto;
import br.com.jacto.trevo.repository.ProductRepository;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import br.com.jacto.trevo.model.Product;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.hibernate.reactive.mutiny.Mutiny;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/product")
@WithSession
public class ProductResource {

    @Inject
    ProductRepository productRepository;

    @GET
    public Uni<List<ProductDto>> get() {
        return productRepository.listAll().onItem().transform(products -> products.stream().map(ProductDto::new).toList());
    }

    @GET
    @Path("/{productName}")
    public Uni<ProductDetailDto> getProductName(String productName){
        return productRepository.findByName(productName)
                .onItem().transform(product -> new ProductDetailDto(product.getProductName(),product.getAreaSize(),product.getDescription(),product.getCulture(), product.getOrders()));
    }

    @POST
    public Uni<Response> create(Product product) {
        return Panache.<Product>withTransaction(product::persist)
                .onItem().transform(inserted -> Response.created(URI.create("/product/" + inserted.getProductName())).build());
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> delete(UUID id) {
        return productRepository.deleteById(id).onItem().transform(delete -> delete ? Response.noContent().build() :
                Response.status(Response.Status.NOT_FOUND).build());
    }
}
