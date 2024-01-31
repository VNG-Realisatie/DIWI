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
    private String[] municipalityRole;
    private String[] planningPlanStatus;
    private Long totalValue;
    private String[] municipality;
    private String[] wijk;
    private String[] buurt;

}
