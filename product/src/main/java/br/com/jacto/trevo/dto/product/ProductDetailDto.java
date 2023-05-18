package br.com.jacto.trevo.dto.product;

import br.com.jacto.trevo.model.Culture;
import br.com.jacto.trevo.model.Product;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductDetailDto {
    private String productName;
    private Double areaSize;
    private String description;
    private List<Culture> cultures;

    public ProductDetailDto() {
    }

    public ProductDetailDto(Product product) {
        this.productName = product.getProductName();
        this.areaSize = product.getAreaSize();
        this.description = product.getDescription();
        this.cultures = product.getCultures().stream().map(Culture::new).collect(Collectors.toList());
    }
}
