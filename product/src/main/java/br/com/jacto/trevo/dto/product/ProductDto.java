package br.com.jacto.trevo.dto.product;

import br.com.jacto.trevo.model.Product;
import lombok.Data;

@Data
public class ProductDto {
    public ProductDto() {
    }

    public ProductDto(Product product) {
        this.productName = product.getProductName();
        this.areaSize = product.getAreaSize();
        this.description = product.getDescription();
    }


    private String productName;

    private Double areaSize;

    private String description;


}
