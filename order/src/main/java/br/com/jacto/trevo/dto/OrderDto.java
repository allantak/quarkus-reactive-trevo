package br.com.jacto.trevo.dto;

import br.com.jacto.trevo.model.OrderItem;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderDto {

    public OrderDto(OrderItem orderItem){
        setPhone(orderItem.getPhone());
        setEmail(orderItem.getEmail());
        setClientName(orderItem.getClientName());
        setProduct(orderItem.getProduct());
    }

    private String clientName;

    private String email;

    private String phone;

    private String product;

}
