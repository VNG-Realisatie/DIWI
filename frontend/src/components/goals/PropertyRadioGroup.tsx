import { FormControl, FormLabel, RadioGroup, FormControlLabel, Radio } from "@mui/material";

type PropertyRadioGroupProps = {
    property: string;
};

export const PropertyRadioGroup = ({ property }: PropertyRadioGroupProps) => {
    switch (property) {
        case "PROPERTY":
            return (
                <FormControl component="fieldset">
                    <FormLabel component="legend">Custom property</FormLabel>
                    <RadioGroup row name="customProperty">
                        <FormControlLabel value="Ja" control={<Radio />} label="Ja" />
                        <FormControlLabel value="Nee" control={<Radio />} label="Nee" />
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
