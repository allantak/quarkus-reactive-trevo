package br.com.jacto.trevo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.pgclient.PgPool;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
public class OrderItem extends PanacheEntityBase {

    public OrderItem() {}

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
    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private Product product;

    public static Uni<OrderItem> findByEmail(String email){
        return find("email", email).firstResult();
    }


}
