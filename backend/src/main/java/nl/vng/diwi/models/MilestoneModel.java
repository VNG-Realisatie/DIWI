package nl.vng.diwi.models;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import nl.vng.diwi.dal.entities.Milestone;
import nl.vng.diwi.dal.entities.MilestoneState;
import nl.vng.diwi.dal.entities.enums.MilestoneStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MilestoneModel {

    private UUID id;

    private MilestoneStatus state;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate date;

    private String description;

    public MilestoneModel(Milestone milestone) {
        this.setId(milestone.getId());
        MilestoneState milestoneState = milestone.getState().get(0);
        this.setDescription(milestoneState.getDescription());
        this.setDate(milestoneState.getDate());
        this.setState(milestoneState.getState());
    }
}
