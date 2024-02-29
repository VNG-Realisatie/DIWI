package nl.vng.diwi.models.superclasses;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.models.OrganizationModel;
import nl.vng.diwi.models.PriorityModel;
import nl.vng.diwi.models.SelectModel;

import lombok.Data;

@Data
@EqualsAndHashCode(callSuper = true)
abstract public class ProjectSnapshotModelSuperclass extends DatedDataModelSuperClass {

    private UUID projectId;

    private String projectName;
    private String projectColor;
    private Confidentiality confidentialityLevel;
    private List<PlanType> planType = new ArrayList<>();
    private PriorityModel priority = new PriorityModel();
    private ProjectPhase projectPhase;
    private List<PlanStatus> planningPlanStatus;
    private List<SelectModel> municipalityRole = new ArrayList<>();
    private List<OrganizationModel> projectOwners = new ArrayList<>();
    private List<OrganizationModel> projectLeaders = new ArrayList<>();

    private Long totalValue;
    private List<SelectModel> municipality = new ArrayList<>();
    private List<SelectModel> wijk = new ArrayList<>();
    private List<SelectModel> buurt = new ArrayList<>();

}
