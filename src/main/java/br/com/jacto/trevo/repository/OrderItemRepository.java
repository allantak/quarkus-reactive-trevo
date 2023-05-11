package br.com.jacto.trevo.repository;

import br.com.jacto.trevo.model.OrderItem;
import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class OrderItemRepository implements PanacheRepositoryBase<OrderItem, UUID> {
}
