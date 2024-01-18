package com.vng.models;

import com.vng.dal.entities.enums.Confidentiality;
import com.vng.dal.entities.enums.PlanType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class ProjectListModel {

    private String projectId;
    private String projectStateId;
    private String projectName; //project_name_changelog_id
    private String projectColor; //from project_state
    private Confidentiality confidentialityLevel;  //from project_state
    private String organizationName; //project_state -> organization -> organization_state
    private List<PlanType> planType;  //"project_plan_type_changelog_id           project_plan_type_value_id"
    private LocalDate endDate; //milestone dates from project_duration_changelog
    private LocalDate startDate; //milestone dates project_duration_changelog
    private List<String> priority; // "concat ordinal_level + value_level"" => 1 or 2 entries depending on change_log value_type in project_priorisering_changelog_id
    private String projectPhase;    //project_fase_changelog_id
    private List<String> municipalityRole;    //project_gemeenterol_changelog_id => can return multiple values so multiple project_gemeenterol_value_id => each referenced one project_gemeenterol_value_state_id
    private List<String> planningPlanStatus;  // project_planologische_planstatus_changelog_id    project_planologische_planstatus_changelog_value_id
//    private boolean programmering;    //ignore for now - is in housing tables
//    private List<String> projectLeader;  // from -> project_actor...  //ignore for now
}
