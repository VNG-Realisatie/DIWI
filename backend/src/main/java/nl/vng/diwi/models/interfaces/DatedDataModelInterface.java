package nl.vng.diwi.models.interfaces;

import nl.vng.diwi.models.LocalDateModel;

public interface DatedDataModelInterface {
    public LocalDateModel getStartDate();
    public void setStartDate(LocalDateModel startDate);
    public LocalDateModel getEndDate();
    public void setEndDate(LocalDateModel endDate);
}
