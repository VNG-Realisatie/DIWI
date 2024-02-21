package nl.vng.diwi.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class OrganizationModel {

    UUID uuid;
    String name;
    List<OrganizationUserModel> users = new ArrayList<>();

}
