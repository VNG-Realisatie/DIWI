package com.vng.models.superclasses;

import com.vng.dal.entities.Milestone;
import com.vng.models.LocalDateModel;
import com.vng.models.MilestoneModel;

import lombok.Data;

@Data
abstract public class DatedDataModelSuperClass {
    private LocalDateModel startDate;
    private LocalDateModel endDate;
    
    public void setStartDate(MilestoneModel milestone) {
        startDate = milestone.getDate();
    }

    public void setStartDate(LocalDateModel date) {
        startDate = date;
    }
    
    public void setStartDate(Milestone milestone) {
        startDate = (new MilestoneModel(milestone)).getDate();
    }
    
    public void setEndDate(MilestoneModel milestone) {
        endDate = milestone.getDate();
    }

    public void setEndDate(LocalDateModel date) {
        endDate = date;
    }

    public void setEndDate(Milestone milestone) {
        endDate = (new MilestoneModel(milestone)).getDate();
    }
}
