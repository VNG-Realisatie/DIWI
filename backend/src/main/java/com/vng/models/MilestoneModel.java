package com.vng.models;

import java.util.UUID;

import com.vng.dal.entities.enums.MilestoneStatus;

import lombok.Data;

@Data
public class MilestoneModel {
	private UUID id;
	private MilestoneStatus state;
	private LocalDateModel date;
	private String description;
}
