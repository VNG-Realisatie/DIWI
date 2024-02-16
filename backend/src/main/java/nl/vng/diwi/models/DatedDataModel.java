package nl.vng.diwi.models;

import nl.vng.diwi.models.superclasses.DatedDataModelSuperClass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class DatedDataModel<T> extends DatedDataModelSuperClass {
    private T data;
}
