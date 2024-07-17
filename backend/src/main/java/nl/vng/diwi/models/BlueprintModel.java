package nl.vng.diwi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.enums.BlueprintElement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class BlueprintModel {

    @JsonProperty(required = true)
    UUID uuid;

    @JsonProperty(required = true)
    String name;

    List<UserGroupModel> userGroups = new ArrayList<>();

    List<BlueprintElement> elements = new ArrayList<>();

}
