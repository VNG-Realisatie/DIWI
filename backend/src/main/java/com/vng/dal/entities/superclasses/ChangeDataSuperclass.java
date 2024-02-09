package com.vng.dal.entities.superclasses;

import com.vng.dal.entities.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ChangeDataSuperclass extends IdSuperclass {

    @Column(name = "change_end_date")
    private ZonedDateTime changeEndDate;

    @Column(name = "change_start_date")
    private ZonedDateTime changeStartDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_user_id")
    private User changeUser;
}
