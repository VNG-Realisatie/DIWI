package nl.vng.diwi.models.interfaces;

import java.time.LocalDate;

public interface DatedDataModelInterface {
    public LocalDate getStartDate();
    public void setStartDate(LocalDate startDate);
    public LocalDate getEndDate();
    public void setEndDate(LocalDate endDate);
}
