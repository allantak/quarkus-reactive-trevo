package br.com.jacto.trevo.dto.product;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;
@Data
public class ProductForm {

    @NotBlank(message = "Deve preencher o campo productName")
    private String productName;

    private Double areaSize;

    @NotBlank(message = "Deve preencher o campo description")
    private String description;
}
