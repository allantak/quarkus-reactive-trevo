package br.com.jacto.trevo.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderItem extends PanacheEntityBase {

  public OrderItem() {}

  public OrderItem(OrderItem orderItem) {
    this.clientName = orderItem.clientName;
    this.email = orderItem.email;
    this.phone = orderItem.phone;
  }

  public OrderItem(String clientName, String email, String phone) {
    setEmail(email);
    setPhone(phone);
    setClientName(clientName);
  }

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID orderItemId;

  private String clientName;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false, unique = true)
  private String phone;

  public static String convertToString(OrderItem orderItem)
    throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString(orderItem);
  }
}
