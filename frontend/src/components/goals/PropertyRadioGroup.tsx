import { FormControl, FormLabel, FormGroup, FormControlLabel, Checkbox } from "@mui/material";
import { Goal } from "../../api/goalsServices";
import { ChangeEvent } from "react";
import { options } from "./constants";

type PropertyRadioGroupProps = {
    property: string;
    setGoal: (goal: Goal) => void;
    goal: Goal;
};

const formatLabel = (option: string) => {
    return option.charAt(0) + option.slice(1).toLowerCase().replace(/_/g, " ");
};

export const PropertyRadioGroup = ({ property, setGoal, goal }: PropertyRadioGroupProps) => {
    const handleCheckboxChange = (event: ChangeEvent<HTMLInputElement>) => {
        const { value, checked } = event.target;

        if (property === "PROGRAMMING") {
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
        } else {
            const currentOptions = goal.conditions[0].listOptions || [];
            let updatedOptions;

            if (checked) {
                updatedOptions = [...currentOptions, value];
            } else {
                updatedOptions = currentOptions.filter((item) => item !== value);
            }

            setGoal({
                ...goal,
                conditions: [
                    {
                        ...goal.conditions[0],
                        listOptions: updatedOptions,
                    },
                ],
            });
        }
    };

    switch (property) {
        case "PROGRAMMING":
            return (
                <FormControl component="fieldset">
                    <FormGroup row>
                        <FormControlLabel
                            control={<Checkbox checked={goal.conditions[0].booleanValue === true} onChange={handleCheckboxChange} value="true" />}
                            label="Ja"
                        />
                        <FormControlLabel
                            control={<Checkbox checked={goal.conditions[0].booleanValue === false} onChange={handleCheckboxChange} value="false" />}
                            label="Nee"
                        />
                    </FormGroup>
                </FormControl>
            );
        case "GROUND_POSITION":
        case "HOUSE_TYPE":
            return (
                <FormControl component="fieldset">
                    <FormGroup>
                        {options[property].map((option) => (
                            <FormControlLabel
                                key={option}
                                control={
                                    <Checkbox
                                        checked={goal.conditions[0].listOptions?.includes(option) || false}
                                        onChange={handleCheckboxChange}
                                        value={option}
                                    />
                                }
                                label={formatLabel(option)}
                            />
                        ))}
                    </FormGroup>
                </FormControl>
            );
        default:
            return null;
    }
};
