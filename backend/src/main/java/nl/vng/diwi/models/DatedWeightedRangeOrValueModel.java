package nl.vng.diwi.models;

import nl.vng.diwi.models.interfaces.WeightedRangeModelInterface;
import nl.vng.diwi.models.interfaces.WeightedValueModelInterface;
import nl.vng.diwi.models.superclasses.DatedDataModelSuperClass;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class DatedWeightedRangeOrValueModel<T> extends DatedDataModelSuperClass implements WeightedValueModelInterface<T>, WeightedRangeModelInterface<T> {
    private Integer levelMin;
    private Integer levelMax;
    private Integer level;
    private T dataMin;
    private T dataMax;
    private T data;

    public void setMin(Integer level, T data) {
        this.level = null;
        this.data = null;
        levelMin = level;
        dataMin = data;
    }

    public void setMax(Integer level, T data) {
        this.level = null;
        this.data = null;
        levelMax = level;
        dataMax = data;
    }

    public Integer getLevelMin() {
        return levelMin;
    }

    public void setLevelMin(Integer levelMin) {
        this.levelMin = levelMin;
    }

    public Integer getLevelMax() {
        return levelMax;
    }

    public void setLevelMax(Integer levelMax) {
        this.levelMax = levelMax;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public T getDataMin() {
        return dataMin;
    }

    public void setDataMin(T dataMin) {
        this.dataMin = dataMin;
    }

    public T getDataMax() {
        return dataMax;
    }

    public void setDataMax(T dataMax) {
        this.dataMax = dataMax;
    }
    
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
