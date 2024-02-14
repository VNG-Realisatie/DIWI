package com.vng.models;

import com.vng.models.superclasses.DatedDataModelSuperClass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class DatedDataModel<T> extends DatedDataModelSuperClass {
    private T data;
}
