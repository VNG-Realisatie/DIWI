package nl.vng.diwi.models;

import nl.vng.diwi.models.superclasses.DatedDataModelSuperClass;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DatedDataModel<T> extends DatedDataModelSuperClass {

    public DatedDataModel(T data, LocalDate startDate, LocalDate endDate) {
        super(startDate, endDate);
        this.data = data;
    }

    private UUID id;
    private T data;
}
