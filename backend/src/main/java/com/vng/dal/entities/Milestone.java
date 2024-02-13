package com.vng.dal.entities;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

import java.util.List;

import org.hibernate.annotations.Filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vng.dal.entities.superclasses.IdSuperclass;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "milestone", schema = VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Milestone extends IdSuperclass {
    
    @JsonIgnoreProperties("milestone")
    @OneToMany(mappedBy="milestone", fetch = FetchType.LAZY)
    @Filter(name = "current", condition = "change_end_date IS NULL")
    private List<MilestoneState> state;

}
