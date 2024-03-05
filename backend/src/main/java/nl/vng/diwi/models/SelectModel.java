package nl.vng.diwi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectModel implements Comparable<SelectModel> {

    private UUID id;
    private String name;


    @Override
    public int compareTo(SelectModel o) {
        return this.name.compareToIgnoreCase(o.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SelectModel that = (SelectModel) o;

        return Objects.equals(this.id, that.id);
    }

}
