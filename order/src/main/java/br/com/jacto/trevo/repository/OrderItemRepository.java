package br.com.jacto.trevo.repository;

import br.com.jacto.trevo.model.OrderItem;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class OrderItemRepository implements PanacheRepositoryBase<OrderItem, UUID> {
    public Uni<OrderItem> findByEmail(String email) {
        return find("email", email).firstResult();
    }
    public Uni<Long> deleteByEmail(String email) {
        return delete("email", email);
    }

}
