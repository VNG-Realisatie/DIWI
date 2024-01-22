package com.vng.models;

import com.vng.dal.entities.enums.Confidentiality;
import lombok.Data;

import java.util.UUID;

@Data
public class ProjectListModel {

    private UUID projectId;
    private UUID projectStateId;
    private String projectName;
    private String projectColor;
    private Confidentiality confidentialityLevel;
    private String organizationName;
    private String[] planType;
    private String startDate;
    private String endDate;
    private String[] priority;
    private String projectPhase;
    private String[] municipalityRole;  //project_gemeenterol_changelog_id => can return multiple valid values
    private String[] planningPlanStatus;
//    private boolean programmering;    //ignore for now - is in housing tables
//    private List<String> projectLeader;  // from -> project_actor...  //ignore for now
}
