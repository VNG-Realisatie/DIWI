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
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
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

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[][]")
    private String[][] priority;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private ProjectPhase projectPhase;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[][]")
    private String[][] municipalityRole;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    @Getter(AccessLevel.NONE)
    private List<PlanStatus> planningPlanStatus;

    private Long totalValue;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[][]")
    private String[][] municipality;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[][]")
    private String[][] wijk;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[][]")
    private String[][] buurt;

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

}
