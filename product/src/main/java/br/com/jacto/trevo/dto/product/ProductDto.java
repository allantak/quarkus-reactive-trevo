package br.com.jacto.trevo.dto.product;

import br.com.jacto.trevo.model.Product;
import lombok.Data;

@Data
public class ProductDto {
    private String productName;
    private Double areaSize;
    private String description;

    public ProductDto() {
    }

    public ProductDto(Product product) {
        this.productName = product.getProductName();
        this.areaSize = product.getAreaSize();
        this.description = product.getDescription();
    }


}
