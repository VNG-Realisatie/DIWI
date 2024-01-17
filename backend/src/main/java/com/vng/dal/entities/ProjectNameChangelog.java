package com.vng.dal.entities;

import com.vng.dal.CustomUuidGeneration;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

@Entity
@Table(name = "project_name_changelog", schema = VNG_SCHEMA_NAME)
public class ProjectNameChangelog {

    @Id
    @GeneratedValue
    @CustomUuidGeneration
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "start_milestone_id")
    private Milestone startMilestone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "end_milestone_id")
    private Milestone endMilestone;

    @Column(name = "change_end_date")
    private LocalDateTime changeEndDate;

    @Column(name = "change_start_date")
    private LocalDateTime changeStartDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_user_id")
    private User changeUser;

    public ProjectNameChangelog() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Milestone getStartMilestone() {
        return startMilestone;
    }

    public void setStartMilestone(Milestone startMilestone) {
        this.startMilestone = startMilestone;
    }

    public Milestone getEndMilestone() {
        return endMilestone;
    }

    public void setEndMilestone(Milestone endMilestone) {
        this.endMilestone = endMilestone;
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
