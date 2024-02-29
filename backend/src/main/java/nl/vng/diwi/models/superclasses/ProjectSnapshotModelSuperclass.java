package nl.vng.diwi.models.superclasses;

import java.time.LocalDate;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.models.OrganizationModel;
//import nl.vng.diwi.models.PriorityModel;
//import nl.vng.diwi.models.WeightedRangeOrValueModel;
import nl.vng.diwi.models.interfaces.DatedDataModelInterface;
import nl.vng.diwi.models.interfaces.ProjectSnapshotModelInterface;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import lombok.Data;

@Data
abstract public class ProjectSnapshotModelSuperclass implements ProjectSnapshotModelInterface, DatedDataModelInterface {

    private UUID projectId;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate startDate;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate endDate;

    private String projectName;
    private String projectColor;
    private Confidentiality confidentialityLevel;
    private List<PlanType> planType = new ArrayList<>();
    //private List<PriorityModel> priority = Arrays.asList(new PriorityModel[3]);
    private ProjectPhase projectPhase;
    private List<String> municipalityRole = new ArrayList<>();
    private List<PlanStatus> planningPlanStatus = new ArrayList<>();
    private List<OrganizationModel> projectOwners = new ArrayList<>();
    private List<OrganizationModel> projectLeaders = new ArrayList<>();
/*
    public void setPriority(WeightedRangeOrValueModel<String> priority) {
        this.priority.set(0, new PriorityModel(priority.getLevelMin(), priority.getDataMin()));
        this.priority.set(1, new PriorityModel(priority.getLevel(), priority.getData()));
        this.priority.set(2, new PriorityModel(priority.getLevelMax(), priority.getDataMax()));
    }

    public List<PriorityModel> getPriority() {
        return priority;
    }

    public void setPriority(List<PriorityModel> priority) {
        this.priority = priority;
    }
*/
}
