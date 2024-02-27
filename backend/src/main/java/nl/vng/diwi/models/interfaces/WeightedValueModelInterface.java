package nl.vng.diwi.models.interfaces;

public interface WeightedValueModelInterface<T> {
    public T getData();
    public void setData(T data);
    public Integer getLevel();
    public void setLevel(Integer level);
}
