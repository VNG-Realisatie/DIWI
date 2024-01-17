package com.vng.dal.entities;

import com.vng.dal.entities.enums.Confidentiality;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

@Entity
@Table(name = "project_state", schema = VNG_SCHEMA_NAME)
public class ProjectState {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_organization_id")
    private Organization ownerOrganization;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "confidentiality_level")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Confidentiality confidentiality;

    @Column(name = "change_end_date")
    private LocalDateTime changeEndDate;

    @Column(name = "change_start_date")
    private LocalDateTime changeStartDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_user_id")
    private User changeUser;

    @Column(name = "project_color")
    private String color;

    public ProjectState() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Organization getOwnerOrganization() {
        return ownerOrganization;
    }

    public void setOwnerOrganization(Organization ownerOrganization) {
        this.ownerOrganization = ownerOrganization;
    }

    public Confidentiality getConfidentiality() {
        return confidentiality;
    }

    public void setConfidentiality(Confidentiality confidentiality) {
        this.confidentiality = confidentiality;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
