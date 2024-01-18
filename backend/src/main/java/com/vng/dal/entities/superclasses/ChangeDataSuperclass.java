package com.vng.dal.entities.superclasses;

import com.vng.dal.entities.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@MappedSuperclass
@Data
@NoArgsConstructor
public class ChangeDataSuperclass extends IdSuperclass {

    @Column(name = "change_end_date")
    private LocalDateTime changeEndDate;

    @Column(name = "change_start_date")
    private LocalDateTime changeStartDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_user_id")
    private User changeUser;
}
