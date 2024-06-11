package nl.vng.diwi.models;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImportResponse {
    private List<SelectModel> result;
    private List<ImportError> error;
}
