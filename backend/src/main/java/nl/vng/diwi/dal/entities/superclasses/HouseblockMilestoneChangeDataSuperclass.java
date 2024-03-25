package nl.vng.diwi.dal.entities.superclasses;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.entities.Houseblock;
import nl.vng.diwi.generic.CopyObject;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class HouseblockMilestoneChangeDataSuperclass extends MilestoneChangeDataSuperclass implements CopyObject {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "woningblok_id")
    private Houseblock houseblock;

}
