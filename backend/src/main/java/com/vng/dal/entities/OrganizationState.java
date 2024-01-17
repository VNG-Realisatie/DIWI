package com.vng.dal.entities;

import com.vng.dal.CustomUuidGeneration;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

@Entity
@Table(name = "organization_state", schema = VNG_SCHEMA_NAME)
public class OrganizationState {

    @Id
    @GeneratedValue
    @CustomUuidGeneration
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_organization_id")
    private Organization parentOrganization;

    @Column(name = "naam")
    private String name;

    @Column(name = "change_end_date")
    private LocalDateTime changeEndDate;

    @Column(name = "change_start_date")
    private LocalDateTime changeStartDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_user_id")
    private User changeUser;

    public OrganizationState() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Organization getParentOrganization() {
        return parentOrganization;
    }

    public void setParentOrganization(Organization parentOrganization) {
        this.parentOrganization = parentOrganization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getChangeEndDate() {
        return changeEndDate;
    }

    public void setChangeEndDate(LocalDateTime changeEndDate) {
        this.changeEndDate = changeEndDate;
    }

    public LocalDateTime getChangeStartDate() {
        return changeStartDate;
    }

    public void setChangeStartDate(LocalDateTime changeStartDate) {
        this.changeStartDate = changeStartDate;
    }

    public User getChangeUser() {
        return changeUser;
    }

    public void setChangeUser(User changeUser) {
        this.changeUser = changeUser;
    }
}
