package com.vng.dal.entities;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

import com.vng.dal.entities.superclasses.IdSuperclass;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user", schema = VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class User extends IdSuperclass {

}
