package br.com.jacto.trevo.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
public class Product extends PanacheEntityBase {

    public Product(){
    }

    public Product(UUID productId, String productName, Double areaSize, String description, String culture){
        setProductId(productId);
        setProductName(productName);
        setAreaSize(areaSize);
        setDescription(description);
        setCulture(culture);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productId;

    @Column(nullable = false, unique = true)
    private String productName;

    private Double areaSize;

    private String description;

    private String culture;

    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> orders = new ArrayList<>();

    public static Uni<Product> findByName(String productName){
        return find("productName", productName).firstResult();
    }

}