package br.com.jacto.trevo.dto.culture;

import br.com.jacto.trevo.model.Culture;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
public class CultureDto {

    public CultureDto(Culture culture){
        this.cultureId = culture.getCultureId();
        this.cultureName = culture.getCultureName();
    }

    private UUID cultureId;

    private String cultureName;


}
