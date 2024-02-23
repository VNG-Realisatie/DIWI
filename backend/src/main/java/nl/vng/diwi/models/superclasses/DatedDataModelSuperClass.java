package nl.vng.diwi.models.superclasses;

import nl.vng.diwi.dal.entities.Milestone;
import nl.vng.diwi.models.LocalDateModel;
import nl.vng.diwi.models.MilestoneModel;
import nl.vng.diwi.models.interfaces.DatedDataModelInterface;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
@EqualsAndHashCode
abstract public class DatedDataModelSuperClass implements DatedDataModelInterface {
    private LocalDateModel startDate;
    private LocalDateModel endDate;

    public LocalDateModel getStartDate() {
        return startDate;
    }

    public void setStartDate(MilestoneModel milestone) {
        startDate = milestone.getDate();
    }

    public void setStartDate(LocalDateModel date) {
        startDate = date;
    }

    public void setStartDate(Milestone milestone) {
        startDate = (new MilestoneModel(milestone)).getDate();
    }

    public LocalDateModel getEndDate() {
        return endDate;
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
