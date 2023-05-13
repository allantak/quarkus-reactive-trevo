package br.com.jacto.trevo.dto.orderItem;

import br.com.jacto.trevo.model.OrderItem;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderDto {

    public OrderDto(OrderItem orderItem){
        setPhone(orderItem.getPhone());
        setEmail(orderItem.getEmail());
        setClientName(orderItem.getClientName());
    }

    private String clientName;

    private String email;

    private String phone;

}
