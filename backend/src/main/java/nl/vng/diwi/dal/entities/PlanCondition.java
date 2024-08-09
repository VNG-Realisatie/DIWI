package nl.vng.diwi.dal.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;

import java.util.List;

@Entity
@Table(name = "plan_conditie", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class PlanCondition extends IdSuperclass {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @OneToMany(mappedBy="planCondition", fetch = FetchType.LAZY)
    private List<PlanConditionState> states;

    @OneToMany(mappedBy="planCondition", fetch = FetchType.LAZY)
    private List<PlanConditionGroundPosition> groundPositions;

    @OneToMany(mappedBy="planCondition", fetch = FetchType.LAZY)
    private List<PlanConditionTargetGroup> targetGroups;

    @OneToMany(mappedBy="planCondition", fetch = FetchType.LAZY)
    private List<PlanConditionAppearanceAndType> appearanceAndTypes;

    @OneToMany(mappedBy="planCondition", fetch = FetchType.LAZY)
    private List<PlanConditionOwnershipValue> ownershipValues;

    @OneToMany(mappedBy="planCondition", fetch = FetchType.LAZY)
    private List<PlanConditionProgramming> programmings;

    @OneToMany(mappedBy="planCondition", fetch = FetchType.LAZY)
    private List<PlanConditionBooleanCustomProperty> booleanCustomProperties;

    @OneToMany(mappedBy="planCondition", fetch = FetchType.LAZY)
    private List<PlanConditionCategoryCustomProperty> categoryCustomProperties;

    @OneToMany(mappedBy="planCondition", fetch = FetchType.LAZY)
    private List<PlanConditionOrdinalCustomProperty> ordinalCustomProperties;

    @OneToMany(mappedBy="planCondition", fetch = FetchType.LAZY)
    private List<PlanConditionRegistryLink> registryLinks;

}
