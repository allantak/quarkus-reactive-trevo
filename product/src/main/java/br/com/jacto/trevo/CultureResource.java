package br.com.jacto.trevo;

import br.com.jacto.trevo.dto.culture.CultureDto;
import br.com.jacto.trevo.dto.culture.CultureForm;
import br.com.jacto.trevo.model.Culture;
import br.com.jacto.trevo.repository.CultureRepository;
import br.com.jacto.trevo.repository.ProductRepository;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/culture")
@WithSession
@Authenticated
public class CultureResource {

    @Inject
    CultureRepository cultureRepository;

    @Inject
    ProductRepository productRepository;

    @Inject
    Validator validator;

    private static final Logger LOG = Logger.getLogger(CultureResource.class);

    @GET
    public Uni<List<CultureDto>> get() {
        return cultureRepository.listAll()
                .onItem().transform(orderItems -> orderItems.stream().map(CultureDto::new).collect(Collectors.toList()))
                .onItem().invoke(LOG::info);
    }

    @GET
    @Path("/{id}")
    public Uni<Response> getId(UUID id) {
        return cultureRepository.findById(id)
                .onItem().ifNotNull().transform(culture -> {
                    CultureDto cultureDto = new CultureDto(culture);
                    return Response.ok(cultureDto).build();
                })
                .onItem().invoke(LOG::info)
                .onItem().ifNull().continueWith(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @RolesAllowed("admin")
    public Uni<Response> create(@Valid CultureForm culture) {
        Set<ConstraintViolation<CultureForm>> violations = validator.validate(culture);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Validation failed", violations);
        }
        return productRepository.findByName(culture.getProductName())
                .onItem().ifNotNull().transformToUni(product -> {
                    Culture newCulture = new Culture(culture.getCultureName(), product);
                    LOG.infof("New Culture: %s", newCulture);
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
                    LOG.infof("delete Culture: %s", culture);
                    return Panache.withTransaction(() -> cultureRepository.delete("id", id))
                            .replaceWith(Response.noContent().build());
                })
                .onItem().ifNull().continueWith(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

}
