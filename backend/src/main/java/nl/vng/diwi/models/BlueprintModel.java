package nl.vng.diwi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.BlueprintSqlModel;
import nl.vng.diwi.dal.entities.enums.BlueprintElement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class BlueprintModel {

    UUID uuid;

    @JsonProperty(required = true)
    String name;

    List<UserGroupModel> userGroups = new ArrayList<>();

    List<BlueprintElement> elements = new ArrayList<>();

    public BlueprintModel(BlueprintSqlModel sqlModel) {
        this.uuid = sqlModel.getId();
        this.name = sqlModel.getName();
        this.elements = sqlModel.getElements();
        this.userGroups = UserGroupModel.fromUserGroupUserModelListToUserGroupModelList(sqlModel.getUserModels());
    }

    public String validate() {

        if (this.name == null || this.name.isBlank()) {
            return "Property name can not be null.";
        }
        if (this.userGroups == null) {
            this.userGroups = new ArrayList<>();
        }
        if (this.elements == null) {
            this.elements = new ArrayList<>();
        }

        return null;
    }

}
