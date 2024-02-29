package nl.vng.diwi.models.interfaces;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.models.OrganizationModel;
//import nl.vng.diwi.models.PriorityModel;

public interface ProjectSnapshotModelInterface {
    public UUID getProjectId();
    public void setProjectId(UUID projectId);
    public LocalDate getStartDate();
    public void setStartDate(LocalDate startDate);
    public LocalDate getEndDate();
    public void setEndDate(LocalDate endDate);
    public String getProjectName();
    public void setProjectName(String projectName);
    public String getProjectColor();
    public void setProjectColor(String projectColor);
    public List<PlanType> getPlanType();
    public void setPlanType(List<PlanType> planType);
    public List<? extends Object>/*List<PriorityModel>*/ getPriority();
    //public void setPriority(List<PriorityModel> priority);
    public ProjectPhase getProjectPhase();
    public void setProjectPhase(ProjectPhase projectPhase);
    public List<String> getMunicipalityRole();
    public void setMunicipalityRole(List<String> municipalityRole);
    public List<PlanStatus> getPlanningPlanStatus();
    public void setPlanningPlanStatus(List<PlanStatus> planningPlanStatus);
    public List<OrganizationModel> getProjectOwners();
    public void setProjectOwners(List<OrganizationModel> projectOwners);
    public List<OrganizationModel> getProjectLeaders();
    public void setProjectLeaders(List<OrganizationModel> projectLeaders);
}
