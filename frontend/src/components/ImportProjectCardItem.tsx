import {
    FormControl,
    FormControlLabel,
    Grid,
    Radio,
    RadioGroup,
    Stack,
    Typography,
} from "@mui/material";
import { useState } from "react";
import { ImportHouseBlockCardItem } from "./ImportHouseBlockCardItem";
export const columnTitleStyle = {
    border: "solid 1px #ddd",
    p: 0.6,
    fontWeight: 600,
};
export const ImportProjectCardItem = (props: any) => {
    const [value, setValue] = useState("new");
    const { project } = props;
    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setValue((event.target as HTMLInputElement).value);
    };

    return (
        <Stack border="solid 2px #ddd" my={1} p={1}>
            <FormControl>
                <RadioGroup
                    sx={{
                        display: "flex",
                        flexDirection: "row",
                        justifyContent: "flex-end",
                    }}
                    aria-labelledby="demo-controlled-radio-buttons-group"
                    name="controlled-radio-buttons-group"
                    value={value}
                    onChange={handleChange}
                >
                    <FormControlLabel
                        value="new"
                        control={<Radio size="small" />}
                        label="Nieuw project"
                    />
                    <FormControlLabel
                        value="copy"
                        control={<Radio size="small" />}
                        label="Koppel aan:"
                    />
                </RadioGroup>
            </FormControl>
            <Stack>
                <Grid container my={2}>
                    <Grid
                        item
                        sm={12}
                        sx={{
                            backgroundColor: project.color,
                            color: "#FFFFFF",
                            p: 1,
                        }}
                        display="flex"
                        justifyContent="space-between"
                    >
                        Naam: {project.name}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Geo</Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project.geo}
                        </Typography>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>
                            Organisatie
                        </Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project.organization}
                        </Typography>
                    </Grid>
                </Grid>
                <Grid container my={2}>
                    {project.houseblocks.map((hb: any) => {
                        return <ImportHouseBlockCardItem hb={hb} />;
                    })}
                </Grid>
            </Stack>
        </Stack>
    );
};
