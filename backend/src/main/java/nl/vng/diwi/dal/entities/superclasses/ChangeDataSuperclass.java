package nl.vng.diwi.dal.entities.superclasses;

import java.time.ZonedDateTime;

import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.User;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
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

    public ZonedDateTime getChangeEndDate() {
        return changeEndDate;
    }

    public void setChangeEndDate(ZonedDateTime changeEndDate) {
        this.changeEndDate = changeEndDate;
    }

    public ZonedDateTime getChangeStartDate() {
        return changeStartDate;
    }

    public void setChangeStartDate(ZonedDateTime changeStartDate) {
        this.changeStartDate = changeStartDate;
    }

    public User getCreateUser() {
        return createUser;
    }

    public void setCreateUser(User createUser) {
        this.createUser = createUser;
    }

    public User getChangeUser() {
        return changeUser;
    }

    public void setChangeUser(User changeUser) {
        this.changeUser = changeUser;
    }
}
