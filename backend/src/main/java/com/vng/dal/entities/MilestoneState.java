package com.vng.dal.entities;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

import java.time.LocalDate;

import com.vng.dal.entities.enums.MilestoneStatus;
import com.vng.dal.entities.superclasses.ChangeDataSuperclass;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "milestone_state", schema = VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MilestoneState extends ChangeDataSuperclass {

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id")
    private Milestone milestone;

	@Column(name = "date")
	private LocalDate date;
	
	@Column(name = "status")
	private MilestoneStatus state;

	@Column(name = "omschrijving")
	private String description;
}
