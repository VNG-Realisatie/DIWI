import { FormControl, FormLabel, RadioGroup, FormControlLabel, Radio } from "@mui/material";
import { Goal } from "../../api/goalsServices";
import { ChangeEvent } from "react";

type PropertyRadioGroupProps = {
    property: string;
    setGoal: (goal: Goal) => void;
    goal: Goal;
};

export const PropertyRadioGroup = ({ property, setGoal, goal }: PropertyRadioGroupProps) => {
    const handleRadioChange = (event: ChangeEvent<HTMLInputElement>) => {
        const { value } = event.target;
        const booleanValue = value === "true";
        setGoal({
            ...goal,
            conditions: [
                {
                    ...goal.conditions[0],
                    booleanValue: booleanValue,
                },
            ],
        });
    };

    switch (property) {
        case "PROPERTY":
            return (
                <FormControl component="fieldset">
                    <FormLabel component="legend">Custom property</FormLabel>
                    <RadioGroup row name="customProperty" value={goal.conditions[0].booleanValue === true ? "true" : "false"} onChange={handleRadioChange}>
                        <FormControlLabel value="true" control={<Radio />} label="Ja" />
                        <FormControlLabel value="false" control={<Radio />} label="Nee" />
                    </RadioGroup>
                </FormControl>
            );
        case "GROUND_POSITION":
            return (
                <FormControl component="fieldset">
                    <FormLabel component="legend">Grond positie</FormLabel>
                    <RadioGroup name="groundPosition">
                        <FormControlLabel value="Formele toestemming grondeigenaar" control={<Radio />} label="Formele toestemming grondeigenaar" />
                        <FormControlLabel value="Intentie medewerking grondeigenaar" control={<Radio />} label="Intentie medewerking grondeigenaar" />
                        <FormControlLabel value="Geen toestemming grondeigenaar" control={<Radio />} label="Geen toestemming grondeigenaar" />
                    </RadioGroup>
                </FormControl>
            );
        case "HOUSE_TYPE":
            return (
                <FormControl component="fieldset">
                    <FormLabel component="legend">Type woning</FormLabel>
                    <RadioGroup name="housingType">
                        <FormControlLabel value="Eengezinswoning" control={<Radio />} label="Eengezinswoning" />
                        <FormControlLabel value="Meergezinswoning" control={<Radio />} label="Meergezinswoning" />
                    </RadioGroup>
                </FormControl>
            );
        case "PROGRAMMING":
            return (
                <FormControl component="fieldset">
                    <FormLabel component="legend">Programmering</FormLabel>
                    <RadioGroup row name="programming">
                        <FormControlLabel value="Ja" control={<Radio />} label="Ja" />
                        <FormControlLabel value="Nee" control={<Radio />} label="Nee" />
                    </RadioGroup>
                </FormControl>
            );
        default:
            return null;
    }
};
