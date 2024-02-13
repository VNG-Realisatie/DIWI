package com.vng.models;

import com.vng.dal.entities.enums.Confidentiality;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Convert(
    attributeName = "multidimensional_array",
    converter = StringArrayType.class
)
@Entity
@Data
public class ProjectListModel {

    public static final List<String> SORTABLE_COLUMNS = List.of("projectName", "projectOwners", "projectLeaders", "confidentialityLevel", "organizationName",
        "planType", "startDate", "endDate", "priority", "projectPhase", "municipalityRole", "planningPlanStatus", "totalValue", "municipality", "wijk", "buurt");
    public static final String DEFAULT_SORT_COLUMN = "startDate";

    @Id
    private UUID projectId;

    private UUID projectStateId;

    private String projectName;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[][]")
    private String[][] projectOwners;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[][]")
    private String[][] projectLeaders;

    private String projectColor;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Confidentiality confidentialityLevel;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> planType;

    private String startDate;

    private String endDate;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> priority;

    private String projectPhase;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> municipalityRole;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> planningPlanStatus;

    private Long totalValue;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> municipality;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> wijk;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> buurt;

}
