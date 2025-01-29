package nl.vng.diwi.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.DataExchangeType;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dataexchange.DataExchangeTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class DataExchangeExportModel {

    private LocalDate exportDate;
    private List<UUID> projectIds;
    private List<Confidentiality> confidentialityLevels;

    public String validate(DataExchangeType type) {
        if (exportDate == null) {
            exportDate = LocalDate.now();
        }
        if (projectIds != null && projectIds.isEmpty()) {
            projectIds = null;
        }
        if (confidentialityLevels != null && confidentialityLevels.isEmpty()) {
            confidentialityLevels = null;
        }

        if (projectIds == null && confidentialityLevels == null) {
            Confidentiality minConfidentiality = DataExchangeTemplate.templates.get(type).getMinimumConfidentiality();
            confidentialityLevels = Arrays.stream(Confidentiality.values())
                .filter(c -> Confidentiality.confidentialityMap.get(c) >= Confidentiality.confidentialityMap.get(minConfidentiality)).toList();
        }

        if (projectIds != null && confidentialityLevels != null) {
            return "Only one of projectIds and confidentialityLevels can be specified";
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
