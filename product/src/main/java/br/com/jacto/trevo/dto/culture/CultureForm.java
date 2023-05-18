package br.com.jacto.trevo.dto.culture;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CultureForm {

    @NotBlank(message = "campo productName deve ser preenchida")
    private String productName;

    @NotBlank(message = "campo cultureName deve ser preenchida")
    private String cultureName;
}
