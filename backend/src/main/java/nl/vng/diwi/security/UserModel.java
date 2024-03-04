package nl.vng.diwi.security;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserModel {

    private Long id;
    private String email;
    private String name;
    private UserRole userRole;
    private Boolean disabled;

    public static UserModel fromEntityToModel(User entity) {
        UserModel model = new UserModel();
        model.setId(entity.getId());
        model.setEmail(entity.getEmail());
        model.setName(entity.getName());
        model.setUserRole(entity.getRole());
        model.setDisabled(entity.isDisabled());

        return model;
    }

}
