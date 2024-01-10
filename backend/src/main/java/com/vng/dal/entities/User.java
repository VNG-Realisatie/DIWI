package com.vng.dal.entities;

import javax.persistence.*;

@Entity
@Table(name = "user", schema = "diwi_testset_simplified")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_ID_seq")
    @SequenceGenerator(name = "user_ID_seq", allocationSize = 1)
    @Column(name = "\"ID\"")
    private Long id;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
