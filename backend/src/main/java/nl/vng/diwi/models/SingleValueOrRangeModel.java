package nl.vng.diwi.models;

import io.hypersistence.utils.hibernate.type.range.Range;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class SingleValueOrRangeModel<T extends Comparable<? super T>> {

    private T value;
    private T min;
    private T max;

    public SingleValueOrRangeModel(T value, T min, T max) {
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public SingleValueOrRangeModel(T value, Range<T> range) {
        this.value = value;
        if (range != null) {
            this.min = range.lower();
            this.max = range.upper();
        }
    }
}
