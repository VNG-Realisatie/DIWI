package nl.vng.diwi.dal.entities.superclasses;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.User;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@FilterDef(name = GenericRepository.CURRENT_DATA_FILTER, defaultCondition = "change_end_date IS NULL")
public class ChangeDataSuperclass extends IdSuperclass {

    @Column(name = "change_end_date")
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER)
    private ZonedDateTime changeEndDate;

    @Column(name = "change_start_date")
    private ZonedDateTime changeStartDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "create_user_id")
    private User createUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "change_user_id")
    private User changeUser;

}
