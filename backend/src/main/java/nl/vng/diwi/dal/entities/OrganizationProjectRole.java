package nl.vng.diwi.dal.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.enums.ProjectRole;
import nl.vng.diwi.dal.entities.superclasses.ChangeDataSuperclass;

import jakarta.persistence.*;

@Entity
@Table(name = "organization_to_project", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class OrganizationProjectRole extends ChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Column(name = "project_rol")
    @Enumerated(EnumType.STRING)
    private ProjectRole projectRole;

}
