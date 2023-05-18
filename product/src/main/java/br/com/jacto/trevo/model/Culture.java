package br.com.jacto.trevo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class Culture extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID cultureId;
    @Column(nullable = true)
    private String cultureName;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private Product product;

    public Culture() {
    }

    public Culture(Culture culture) {
        this.cultureId = culture.getCultureId();
        this.cultureName = culture.getCultureName();
    }

    public Culture(String cultureName, Product product) {
        this.cultureName = cultureName;
        this.product = product;
    }

}
