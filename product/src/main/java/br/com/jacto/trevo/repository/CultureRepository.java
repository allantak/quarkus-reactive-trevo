package br.com.jacto.trevo.repository;

import br.com.jacto.trevo.model.Culture;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class CultureRepository implements PanacheRepositoryBase<Culture, UUID> {
}
