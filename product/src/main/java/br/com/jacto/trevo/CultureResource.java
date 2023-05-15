package br.com.jacto.trevo;

import br.com.jacto.trevo.dto.culture.CultureForm;
import br.com.jacto.trevo.model.Culture;
import br.com.jacto.trevo.repository.CultureRepository;
import br.com.jacto.trevo.repository.ProductRepository;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/culture")
@WithSession
public class CultureResource {

    @Inject
    CultureRepository cultureRepository;

    @Inject
    ProductRepository productRepository;

    @GET
    public Uni<List<Culture>> get() {
        return cultureRepository.listAll().onItem().transform(orderItems -> orderItems.stream().map(Culture::new).collect(Collectors.toList()));
    }

    @GET
    @Path("/{id}")
    public Uni<Response> getId(UUID id) {
        return cultureRepository.findById(id)
                .onItem().ifNotNull().transform(culture -> Response.ok(new Culture(culture)).build())
                .onItem().ifNull().continueWith(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Uni<Response> create(CultureForm culture) {
        return productRepository.findByName(culture.getProductName())
                .onItem().ifNotNull().transformToUni(product -> {
                    Culture newCulture = new Culture(culture.getCultureName(), product);
                    return Panache.withTransaction(newCulture::persist)
                            .replaceWith(Response.created(URI.create("/culture/" + newCulture.getCultureId())).build());
                })
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> delete(UUID id) {
        return cultureRepository.findById(id)
                .onItem().ifNotNull().transformToUni(culture -> {
                    return Panache.withTransaction(() -> cultureRepository.delete("id", id))
                            .replaceWith(Response.noContent().build());
                })
                .onItem().ifNull().continueWith(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

}
