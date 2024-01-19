package com.vng.dal.entities;

import com.vng.dal.entities.superclasses.IdSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

@Entity
@Table(name = "user", schema = VNG_SCHEMA_NAME)
@Data
@NoArgsConstructor
public class User extends IdSuperclass {

}
