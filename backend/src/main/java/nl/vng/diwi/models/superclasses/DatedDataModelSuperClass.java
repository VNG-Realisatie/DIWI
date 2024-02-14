package nl.vng.diwi.models.superclasses;

import nl.vng.diwi.dal.entities.Milestone;
import nl.vng.diwi.models.LocalDateModel;
import nl.vng.diwi.models.MilestoneModel;

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
