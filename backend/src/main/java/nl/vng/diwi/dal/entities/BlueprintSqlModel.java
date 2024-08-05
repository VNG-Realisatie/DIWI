package nl.vng.diwi.dal.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.JsonListType;
import nl.vng.diwi.dal.entities.enums.BlueprintElement;
import nl.vng.diwi.models.UserGroupUserModel;
import nl.vng.diwi.models.UserGroupUserSqlModel;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class BlueprintSqlModel {

    @Id
    private UUID id;

    private String name;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    @Getter(AccessLevel.NONE)
    private List<BlueprintElement> elements;

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<UserGroupUserSqlModel> users;


    public List<BlueprintElement> getElements() {
        if (elements == null) {
            return new ArrayList<>();
        }
        return elements;
    }

    public List<UserGroupUserSqlModel> getUsers() {
        if (users == null) {
            return new ArrayList<>();
        }
        return users;
    }


    public List<UserGroupUserModel> getUserModels() {
        if (users == null) {
            return new ArrayList<>();
        }
        return users.stream().map(UserGroupUserModel::new).toList();
    }

}
