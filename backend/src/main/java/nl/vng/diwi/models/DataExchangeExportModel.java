package nl.vng.diwi.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.DataExchangeType;
import nl.vng.diwi.dal.entities.enums.Confidentiality;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static nl.vng.diwi.dal.entities.DataExchangeType.ESRI_ZUID_HOLLAND;

@Data
@NoArgsConstructor
public class DataExchangeExportModel {

    private static final List<Confidentiality> DEFAULT_CONFIDENTIALITIES = List.of(Confidentiality.EXTERNAL_REGIONAL, Confidentiality.EXTERNAL_GOVERNMENTAL, Confidentiality.PUBLIC);

    private LocalDate exportDate;
    private List<UUID> projectIds;
    private List<Confidentiality> confidentialityLevels;

    public String validate(ConfigModel configModel, DataExchangeType type) {
        if (exportDate == null) {
            exportDate = LocalDate.now();
        }
        if (projectIds != null && projectIds.isEmpty()) {
            projectIds = null;
        }
        if (confidentialityLevels != null && confidentialityLevels.isEmpty()) {
            confidentialityLevels = null;
        }

        if ( projectIds == null && confidentialityLevels == null) {
            this.confidentialityLevels = DEFAULT_CONFIDENTIALITIES;
        }

        if (projectIds != null && confidentialityLevels != null) {
            return "Only one of projectIds and confidentialityLevels can be specified";
        }

        if (ESRI_ZUID_HOLLAND == type && confidentialityLevels != null) {
            for (Confidentiality c : confidentialityLevels) {
                if (Confidentiality.confidentialityMap.get(c) < Confidentiality.confidentialityMap.get(configModel.getMinimumExportConfidentiality())) {
                    return "Selected confidentiality levels are below minimum allowed export confidentiality";
                }
            }
        }

        return null;
    }

    public List<String> getConfidentialityLevelsAsStrings() {
        if (confidentialityLevels != null && !confidentialityLevels.isEmpty()) {
            return confidentialityLevels.stream().map(Confidentiality::name).toList();
        }
        return null;
    }

}
