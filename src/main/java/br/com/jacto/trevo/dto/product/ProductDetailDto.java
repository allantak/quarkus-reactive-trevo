package br.com.jacto.trevo.dto.product;

import br.com.jacto.trevo.dto.orderItem.OrderDto;
import br.com.jacto.trevo.model.OrderItem;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
@Data
public class ProductDetailDto {
    public ProductDetailDto() {
    }

    public ProductDetailDto(String productName, Double areaSize, String description, String culture, List<OrderItem> orders ) {
        this.productName = productName;
        this.areaSize = areaSize;
        this.description = description;
        this.culture = culture;
        this.orders = orders.stream().map(OrderDto::new).collect(Collectors.toList());
    }

    private String productName;

    private Double areaSize;

    private String description;

    private String culture;

    private List<OrderDto> orders;
}
