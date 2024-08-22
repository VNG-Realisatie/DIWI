package nl.vng.diwi.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.ProjectAuditSqlModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode
@NoArgsConstructor
public class ProjectAuditModel {

    private String projectName;
    private UUID projectId;

    private AuditAction action;
    private AuditProperty property;

    private List<String> oldValues;
    private List<String> newValues;

    private String changeUser;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime changeDate;

    public enum AuditProperty {
        project,
        projectName,
        projectConfidentiality,
        projectColor,
        projectStartDate,
        projectEndDate;
    }

    public enum AuditAction {
        CREATE,
        UPDATE,
        DELETE;
    }

    public ProjectAuditModel(ProjectAuditSqlModel entity) {
        this.projectName = entity.getProjectName();
        this.projectId = entity.getProjectId();
        this.property = entity.getPropertyType();
        this.action = entity.getActionType();
        this.oldValues = entity.getOldValues();
        this.newValues = entity.getNewValues();
        this.changeUser = entity.getChangeUser();
        this.changeDate = entity.getChangeDate();
    }
}
