package com.vng.dal.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "organization_state", schema = "diwi_testset_simplified")
public class OrganizationState {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organization_state_ID_seq")
    @SequenceGenerator(name = "organization_state_ID_seq", allocationSize = 1)
    @Column(name = "\"ID\"")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"organization_ID\"")
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"parent_organization_ID\"")
    private Organization parentOrganization;

    @Column(name = "naam")
    private String name;

    @Column(name = "change_end_date")
    private LocalDateTime changeEndDate;

    @Column(name = "change_start_date")
    private LocalDateTime changeStartDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"change_user_ID\"")
    private User changeUser;

    public OrganizationState() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
