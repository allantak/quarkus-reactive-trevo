package br.com.jacto.trevo.dto.product;

import br.com.jacto.trevo.dto.orderItem.OrderDto;
import br.com.jacto.trevo.model.OrderItem;
import br.com.jacto.trevo.model.Product;
import jakarta.persistence.Column;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductDto {
    public ProductDto() {
    }

    public ProductDto(Product product) {
        this.productName = product.getProductName();
        this.areaSize = product.getAreaSize();
        this.description = product.getDescription();
        this.culture = product.getCulture();
    }


    private String productName;

    private Double areaSize;

    private String description;

    private String culture;

}
