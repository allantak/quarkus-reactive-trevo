package br.com.jacto.trevo.repository;

import br.com.jacto.trevo.model.Product;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class ProductRepository implements PanacheRepositoryBase<Product, UUID> {

    public Uni<Product> findByName(String productName) {
        return find("productName", productName).firstResult();
    }

    public Uni<Long> deleteByName(String productName) {
        return delete("productName", productName);
    }


}
