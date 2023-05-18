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
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
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

    private static final Logger LOG = Logger.getLogger(CultureResource.class);
    @Inject
    CultureRepository cultureRepository;
    @Inject
    ProductRepository productRepository;
    @Inject
    Validator validator;

    @GET
    @Operation(summary = "Lista de cultura")
    public Uni<List<CultureDto>> get() {
        return cultureRepository.listAll()
                .onItem().transform(orderItems -> orderItems.stream().map(CultureDto::new).collect(Collectors.toList()))
                .onItem().invoke(LOG::info);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Cultura especifica")
    @APIResponses(value = {
            @APIResponse(responseCode = "404", description = "Not found")
    })
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
    @Operation(summary = "Cadastro de cultura",
            description = "Somente permissao de admin pode acessar esse recurso")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Created")
    })
    public Uni<Response> create(@Valid CultureForm culture) {
        Set<ConstraintViolation<CultureForm>> violations = validator.validate(culture);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Validation failed", violations);
        }
        return productRepository.findByName(culture.getProductName())
                .onItem().ifNotNull().transformToUni(product -> {
                    Culture newCulture = new Culture(culture.getCultureName(), product);
                    LOG.infof("New Culture: %s", newCulture);
                    return Panache.withTransaction(() -> cultureRepository.persist(newCulture))
                            .replaceWith(Response.created(URI.create("/culture/" + newCulture.getCultureId())).build());
                })
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete de cultura",
            description = "Somente permissao de admin pode acessar esse recurso")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Not content"),
            @APIResponse(responseCode = "404", description = "Not found")
    })
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
