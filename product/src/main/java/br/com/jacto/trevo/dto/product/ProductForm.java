package br.com.jacto.trevo.dto.product;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductForm {

    @NotBlank(message = "Deve preencher o campo productName")
    private String productName;

    private Double areaSize;

    @NotBlank(message = "Deve preencher o campo description")
    private String description;
}
