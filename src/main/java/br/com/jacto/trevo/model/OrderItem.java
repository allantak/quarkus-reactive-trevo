package br.com.jacto.trevo.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.pgclient.PgPool;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
public class OrderItem extends PanacheEntityBase implements Serializable {

    public OrderItem() {}

    public OrderItem(OrderItem orderItem) {
        this.clientName = orderItem.clientName;
        this.email = orderItem.email;
        this.phone = orderItem.phone;
        this.product = orderItem.product;
    }

    public OrderItem(String clientName, String email, String phone, Product product){
        setEmail(email);
        setPhone(phone);
        setClientName(clientName);
        setProduct(product);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderItemId;

    private String clientName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, unique = true)
    private String phone;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "productId")
    private Product product;


    public static Uni<OrderItem> findByEmail(String email){
        return find("email", email).firstResult();
    }

    public static String convertToString(OrderItem orderItem) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(orderItem);
    }
}
