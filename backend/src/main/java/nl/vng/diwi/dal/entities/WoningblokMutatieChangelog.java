package nl.vng.diwi.dal.entities;

import java.util.List;

import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "woningblok_mutatie_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class WoningblokMutatieChangelog extends MilestoneChangeDataSuperclass {

    @JsonIgnoreProperties("mutaties")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "woningblok_id")
    private Woningblok woningblok;
    
    @Column(name = "bruto_plancapaciteit")
    private Integer amountCreated;

    @Column(name = "sloop")
    private Integer amountRemoved;
    
    @Column(name = "netto_plancapaciteit")
    private Integer totalAmount;
    
    @JsonIgnoreProperties("mutatieChangelog")
    @OneToMany(mappedBy="mutatieChangelog", fetch = FetchType.EAGER)
    private List<WoningblokMutatieChangelogSoortValue> type;

    public Woningblok getWoningblok() {
        return woningblok;
    }

    public void setWoningblok(Woningblok woningblok) {
        this.woningblok = woningblok;
    }

    public Integer getAmountCreated() {
        return amountCreated;
    }

    public void setAmountCreated(Integer amountCreated) {
        this.amountCreated = amountCreated;
    }

    public Integer getAmountRemoved() {
        return amountRemoved;
    }

    public void setAmountRemoved(Integer amountRemoved) {
        this.amountRemoved = amountRemoved;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<WoningblokMutatieChangelogSoortValue> getTypeValue() {
        return type;
    }

    public void setTypeValue(List<WoningblokMutatieChangelogSoortValue> type) {
        this.type = type;
    }

}
