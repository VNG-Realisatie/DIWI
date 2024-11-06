package nl.vng.diwi.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.enums.Confidentiality;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class DataExchangeExportModel {

    private static final List<Confidentiality> DEFAULT_CONFIDENTIALITIES = List.of(Confidentiality.EXTERNAL_REGIONAL, Confidentiality.EXTERNAL_GOVERNMENTAL, Confidentiality.PUBLIC);

    private LocalDate exportDate;
    private List<UUID> projectIds;
    private List<Confidentiality> confidentialityLevels;

    public String validate() {
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
        return null;
    }

    public List<String> getConfidentialityLevelsAsStrings() {
        if (confidentialityLevels != null && !confidentialityLevels.isEmpty()) {
            return confidentialityLevels.stream().map(Confidentiality::name).toList();
        }
        return null;
    }

}
