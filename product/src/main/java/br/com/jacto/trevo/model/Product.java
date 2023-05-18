package br.com.jacto.trevo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class Product extends PanacheEntityBase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productId;
    @Column(nullable = false, unique = true)
    private String productName;
    private Double areaSize;
    private String description;
    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Culture> cultures;

    public Product() {
    }

    public Product(String productName, Double areaSize, String description) {
        setProductName(productName);
        setAreaSize(areaSize);
        setDescription(description);
    }

    public static Uni<Product> findByName(String productName) {
        return find("productName", productName).firstResult();
    }

}