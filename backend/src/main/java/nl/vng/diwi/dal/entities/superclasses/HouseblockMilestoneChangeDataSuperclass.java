package nl.vng.diwi.dal.entities.superclasses;

import org.apache.commons.lang3.NotImplementedException;
import org.hibernate.Session;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.entities.Houseblock;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class HouseblockMilestoneChangeDataSuperclass extends MilestoneChangeDataSuperclass  {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "woningblok_id")
    private Houseblock houseblock;


    @Override
    public Object getCopyWithoutMilestones(Session session) {
        throw new NotImplementedException();
    }
}
