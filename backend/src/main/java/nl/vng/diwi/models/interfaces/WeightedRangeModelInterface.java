package nl.vng.diwi.models.interfaces;

public interface WeightedRangeModelInterface<T> {
    public void setMin(Integer levelMin, T dataMin);
    public void setMax(Integer levelMax, T dataMax);
    public T getDataMin();
    public void setDataMin(T dataMin);
    public T getDataMax();
    public void setDataMax(T dataMax);
    public Integer getLevelMax();
    public void setLevelMax(Integer levelMax);
    public Integer getLevelMin();
    public void setLevelMin(Integer levelMin);
}
