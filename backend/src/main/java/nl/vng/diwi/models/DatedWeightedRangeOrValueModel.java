package nl.vng.diwi.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class DatedWeightedRangeOrValueModel<T> extends DatedDataModel<T> {
    private Integer levelMin;
    private Integer levelMax;
    private Integer level;
    private T dataMin;
    private T dataMax;

    public void setMin(int level, T data) {
        this.level = null;
        super.setData(null);
        levelMin = level;
        dataMin = data;
    }

    public void setMax(int level, T data) {
        this.level = null;
        super.setData(null);
        levelMax = level;
        dataMax = data;
    }
}
