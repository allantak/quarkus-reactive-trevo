package br.com.jacto.trevo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderCreateForm {

    @NotBlank(message = "Deve colocar o nome do produto")
    private String productName;

    @NotBlank(message = "Deve se colocar o nome do cliente")
    private  String clientName;

    @Email(message = "Deve ser em formato email")
    @NotBlank(message = "Campo mensagem deve ser preenchida")
    private String email;

    @NotBlank(message = "Campo telefone deve ser preenchida")
    private  String phone;

}
