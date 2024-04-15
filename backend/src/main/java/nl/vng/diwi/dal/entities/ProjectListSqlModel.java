package nl.vng.diwi.dal.entities;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.JsonListType;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.models.SelectModel;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Convert(
    attributeName = "multidimensional_array",
    converter = StringArrayType.class
)
@Entity
@NoArgsConstructor
@Getter
@Setter
public class ProjectListSqlModel {

    @Id
    private UUID projectId;

    private UUID projectStateId;

    private String projectName;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[][]")
    private String[][] projectOwnersArray;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[][]")
    private String[][] projectLeadersArray;

    private String projectColor;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Confidentiality confidentialityLevel;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    @Getter(AccessLevel.NONE)
    private List<PlanType> planType;

    private LocalDate startDate;

    private LocalDate endDate;

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<SelectModel> priority;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private ProjectPhase projectPhase;

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<SelectModel> municipalityRole;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    @Getter(AccessLevel.NONE)
    private List<PlanStatus> planningPlanStatus;

    private Long totalValue;

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<SelectModel> municipality;

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<SelectModel> district;

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<SelectModel> neighbourhood;

    private Double latitude;
    private Double longitude;

    public List<PlanType> getPlanType() {
        if (planType == null) {
            return new ArrayList<>();
        }
        return planType;
    }

    public List<PlanStatus> getPlanningPlanStatus() {
        if (planningPlanStatus == null) {
            return new ArrayList<>();
        }
        return planningPlanStatus;
    }

    public List<SelectModel> getMunicipality() {
        if (municipality == null) {
            return new ArrayList<>();
        }
        return municipality;
    }

    public List<SelectModel> getDistrict() {
        if (district == null) {
            return new ArrayList<>();
        }
        return district;
    }

    public List<SelectModel> getNeighbourhood() {
        if (neighbourhood == null) {
            return new ArrayList<>();
        }
        return neighbourhood;
    }

    public List<SelectModel> getPriority() {
        if (priority == null) {
            return new ArrayList<>();
        }
        return priority;
    }

    public List<SelectModel> getMunicipalityRole() {
        if (municipalityRole == null) {
            return new ArrayList<>();
        }
        return municipalityRole;
    }
}
