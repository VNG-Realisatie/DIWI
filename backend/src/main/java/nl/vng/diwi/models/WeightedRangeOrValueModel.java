package nl.vng.diwi.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WeightedRangeOrValueModel<T> {
    private Integer levelMin;
    private Integer levelMax;
    private Integer level;
    private T dataMin;
    private T dataMax;
    private T data;
    
    public WeightedRangeOrValueModel(DatedWeightedRangeOrValueModel<T> datedData) {
        levelMin = datedData.getLevelMin();
        levelMax = datedData.getLevelMax();
        level = datedData.getLevel();
        dataMin = datedData.getDataMin();
        dataMax = datedData.getDataMax();
        data = datedData.getData();
    }

    public void setMin(int level, T data) {
        this.level = null;
        this.data = null;
        levelMin = level;
        dataMin = data;
    }

    public void setMax(int level, T data) {
        this.level = null;
        this.data = null;
        levelMax = level;
        dataMax = data;
    }
}
