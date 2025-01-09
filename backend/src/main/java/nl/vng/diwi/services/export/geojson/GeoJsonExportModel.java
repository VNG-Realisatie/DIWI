package nl.vng.diwi.services.export.geojson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.GroundPosition;
import nl.vng.diwi.dal.entities.enums.HouseType;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.ProjectStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoJsonExportModel {

    @JsonProperty("projectgegevens")
    private GeoJsonProject project;

    @JsonProperty("woning_blokken")
    private List<GeoJsonHouseblock> houseblocks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeoJsonProject {
        @JsonProperty("diwi_id")
        private UUID diwiId;
        @JsonProperty("basisgegevens")
        private BasicProjectData basicProjectData;
        @JsonProperty("projectgegevens")
        private ProjectData projectData;
        @JsonProperty("projectduur")
        private ProjectDuration projectDuration;
        @JsonProperty("locatie")
        private ProjectLocation projectLocation;
        @Builder.Default
        @JsonProperty("projectfasen")
        private Map<ProjectPhase, LocalDate> projectPhasesMap = new HashMap<>();
        @Builder.Default
        @JsonProperty("planologische_planstatus")
        private Map<PlanStatus, LocalDate> projectPlanStatusesMap = new HashMap<>();
        @Builder.Default
        @JsonProperty("maatwerk_projecteigenschappen")
        private Map<String, String> customPropertiesMap = new HashMap<>();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasicProjectData {
        @JsonProperty("identificatie_nr")
        private Integer identificationNo;
        @JsonProperty("naam")
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectData {
        @JsonProperty("plan_soort")
        private PlanType planType;
        @JsonProperty("prioritering")
        private List<String> priority;
        @JsonProperty("rol_gemeente")
        private List<String> municipalityRole;
        @JsonProperty("status")
        private ProjectStatus status;
        @JsonProperty("eigenaar")
        private String owner;
        @JsonProperty("vertrouwelijkheid")
        private Confidentiality confidentialityLevel;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectDuration {
        @JsonProperty("start_project")
        private LocalDate startDate;
        @JsonProperty("eind_project")
        private LocalDate endDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectLocation {
        @JsonProperty("gemeente")
        private List<String> municipality;
        @JsonProperty("wijk")
        private List<String> district;
        @JsonProperty("buurt")
        private List<String> neighbourhood;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeoJsonHouseblock {
        @JsonProperty("diwi_id")
        private UUID diwiId;
        @JsonProperty("naam")
        private String name;
        @JsonProperty("in_programmering")
        private Boolean programming;
        @JsonProperty("mutatiegegevens")
        private MutationData mutationData;
        @JsonProperty("einddatum")
        private LocalDate endDate;
        @JsonProperty("grootte")
        private SizeData size;
        @JsonProperty("waarde")
        private List<OwnershipValueData> ownershipValue;
        @Builder.Default
        @JsonProperty("fysiek_voorkomen")
        private List<PhysicalAppearanceData> physicalAppearanceList = new ArrayList<>();
        @Builder.Default
        @JsonProperty("doelgroep")
        private List<TargetGroupData> targetGroupList = new ArrayList<>();
        @Builder.Default
        @JsonProperty("grondpositie")
        private Map<GroundPosition, Integer> groundPositionsMap = new HashMap<>();
        @Builder.Default
        @JsonProperty("maatwerk_woningeigenschappen")
        private Map<String, String> customPropertiesMap = new HashMap<>();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SizeData {
        @JsonProperty("laag")
        private Double min;
        @JsonProperty("hoog")
        private Double max;
        @JsonProperty("waarde")
        private Double value;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MutationData {
        @JsonProperty("mutatie_type")
        private MutationType mutationType;
        @JsonProperty("woning_type")
        private HouseType houseType;
        @JsonProperty("aantal")
        private Integer amount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TargetGroupData {
        @JsonProperty("doelgroep_categorie")
        private String categoryName;
        @JsonProperty("aantal")
        private Integer amount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhysicalAppearanceData {
        @JsonProperty("fysiek_voorkomen")
        private String categoryName;
        @JsonProperty("aantal")
        private Integer amount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OwnershipValueData {
        @JsonProperty("laag")
        private Double min;
        @JsonProperty("hoog")
        private Double max;
        @JsonProperty("waarde")
        private Double value;
        @JsonProperty("eigendom_type")
        private String ownershipType;
        @JsonProperty("categorie")
        private String categorie;
        @JsonProperty("aantal")
        private Integer amount;
    }

}
