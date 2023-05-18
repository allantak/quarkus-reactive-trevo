package br.com.jacto.trevo.dto.culture;

import br.com.jacto.trevo.model.Culture;
import lombok.Data;

import java.util.UUID;

@Data
public class CultureDto {

    private UUID cultureId;
    private String cultureName;

    public CultureDto(Culture culture) {
        this.cultureId = culture.getCultureId();
        this.cultureName = culture.getCultureName();
    }


}
