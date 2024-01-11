package com.vng.security;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(name = "users_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    private UUID uuid;

    private String email;

    private String name;

    private boolean disabled = false;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "user_role")
    @Type(PostgreSQLEnumType.class)
    private UserRole role;

    private LocalDateTime creationDate = LocalDateTime.now();

    public User() {
    }

    public User(String email, String name, UserRole role) {
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public UUID getUuid() {
        return uuid;
    }

    public User setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public User setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public UserRole getRole() {
        return role;
    }

    public User setRole(UserRole role) {
        this.role = role;
        return this;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public User setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
        return this;
    }

}
