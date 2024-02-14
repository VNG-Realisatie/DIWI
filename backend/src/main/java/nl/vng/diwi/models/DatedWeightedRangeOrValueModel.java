package nl.vng.diwi.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class DatedWeightedRangeOrValueModel<T> extends DatedDataModel<T> {
    private Integer weightMin;
    private Integer weightMax;
    private Integer weight;
    private T dataMin;
    private T dataMax;

    public void setMin(int weight, T data) {
        this.weight = null;
        super.setData(null);
        weightMin = weight;
        dataMin = data;
    }

    public void setMax(int weight, T data) {
        this.weight = null;
        super.setData(null);
        weightMax = weight;
        dataMax = data;
    }
}
