package br.com.jacto.trevo.repository;

import br.com.jacto.trevo.model.OrderItem;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class OrderItemRepository
  implements PanacheRepositoryBase<OrderItem, UUID> {}
