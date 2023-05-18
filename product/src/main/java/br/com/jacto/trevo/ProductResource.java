package br.com.jacto.trevo;

import br.com.jacto.trevo.dto.product.ProductDetailDto;
import br.com.jacto.trevo.dto.product.ProductDto;
import br.com.jacto.trevo.dto.product.ProductForm;
import br.com.jacto.trevo.model.Product;
import br.com.jacto.trevo.repository.ProductRepository;
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
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Path("/product")
@WithSession
public class ProductResource {

    private static final Logger LOG = Logger.getLogger(CultureResource.class);
    @Inject
    ProductRepository productRepository;
    @Inject
    Validator validator;

    @GET
    @Authenticated
    @Operation(summary = "Listagem de produtos")
    public Uni<List<ProductDto>> get() {
        return productRepository.listAll()
                .onItem().transform(products -> products.stream().map(ProductDto::new).toList())
                .onItem().invoke(LOG::info);
    }

    @GET
    @Authenticated
    @Path("/{productName}")
    @Operation(summary = "Produto especifico")
    @APIResponses(value = {
            @APIResponse(responseCode = "404", description = "Not found")
    })
    public Uni<Response> getProductName(String productName) {
        return productRepository.findByName(productName)
                .onItem().ifNotNull().transform(product -> {
                    ProductDetailDto productDto = new ProductDetailDto(product);
                    LOG.info(productDto);
                    return Response.ok(productDto).build();
                })
                .onItem().ifNull().continueWith(() -> Response.status(Response.Status.NOT_FOUND).build());
    }


    @POST
    @RolesAllowed("admin")
    @Operation(summary = "Cadastro de produto",
            description = "Somente permissao de admin pode acessar esse recurso")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Created")
    })
    public Uni<Response> create(@Valid ProductForm product) {
        Set<ConstraintViolation<ProductForm>> violations = validator.validate(product);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Validation failed", violations);
        }
        Product productSave = new Product(product.getProductName(), product.getAreaSize(), product.getDescription());
        return Panache.<Product>withTransaction(productSave::persist)
                .onItem().transform(inserted -> {
                    LOG.info(inserted);
                    return Response.created(URI.create("/product/" + inserted.getProductName())).build();
                });
    }

    @DELETE
    @Path("/{productName}")
    @RolesAllowed("admin")
    @Operation(summary = "Delete product",
            description = "Somente permissao de admin pode acessar esse recurso")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "No content"),
            @APIResponse(responseCode = "404", description = "Not found")
    })
    public Uni<Response> delete(String productName) {
        return productRepository.findByName(productName)
                .onItem().ifNotNull().transformToUni(product -> {
                            LOG.info(product);
                            return productRepository.deleteByName(product.getProductName()).replaceWith(Response.noContent().build());
                        }
                ).onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
    }

    @Incoming("products-in")
    public void receive(Record<UUID, String> record) {
        LOG.info("Pedido feito, Id " + record.key());
        LOG.info("Informação do pedido " + record.value());
    }
}
