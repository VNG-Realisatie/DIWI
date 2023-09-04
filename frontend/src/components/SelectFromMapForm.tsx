import {
    Box,
    InputLabel,
    MenuItem,
    Select,
    Stack,
    Typography,
} from "@mui/material";
import mapform from "../assets/temp/formmap.png";
import wijk from "../api/json/wijk.json";
import buurt from "../api/json/buurt.json";
import gemeente from "../api/json/gemeente.json";

export const SelectFromMapForm = (props: any) => {
    return (
        <Box mt={4} position="relative">
            <Typography variant="h6" fontWeight="600">
                Teken het project in op de kaart
            </Typography>

            <Box
                sx={{
                    backgroundColor: "#FFFFFF",
                    width: "30%",
                    position: "absolute",
                    left: 15,
                    top: 45,
                }}
                p={2}
            >
                <Stack>
                    <InputLabel id="Gemeente">Gemeente</InputLabel>
                    <Select
                        sx={{ width: "100%" }}
                        labelId="gemeente"
                        id="gemeente"
                        value={
                            props.createProjectForm
                                ? props.createProjectForm.gemeente
                                : ""
                        }
                        label="Gemeente"
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                gemeente: e.target.value,
                            })
                        }
                    >
                        {gemeente.map((m) => {
                            return (
                                <MenuItem key={m.ID} value={m.waarde_label}>
                                    {m.waarde_label}
                                </MenuItem>
                            );
                        })}
                    </Select>
                </Stack>
                <Stack>
                    <InputLabel id="Buurt">Buurt</InputLabel>
                    <Select
                        sx={{ width: "100%" }}
                        labelId="buurt"
                        id="buurt"
                        value={
                            props.createProjectForm
                                ? props.createProjectForm.buurt
                                : ""
                        }
                        label="Buurt"
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                buurt: e.target.value,
                            })
                        }
                    >
                        {buurt.map((m) => {
                            return (
                                <MenuItem key={m.ID} value={m.waarde_label}>
                                    {m.waarde_label}
                                </MenuItem>
                            );
                        })}
                    </Select>
                </Stack>
                <Stack>
                    <InputLabel id="wijk">Wijk</InputLabel>
                    <Select
                        sx={{ width: "100%" }}
                        labelId="wijk"
                        id="wijk"
                        value={
                            props.createProjectForm
                                ? props.createProjectForm.wijk
                                : ""
                        }
                        label="Wijk"
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                wijk: e.target.value,
                            })
                        }
                    >
                        {wijk.map((m) => {
                            return (
                                <MenuItem key={m.ID} value={m.waarde_label}>
                                    {m.waarde_label}
                                </MenuItem>
                            );
                        })}
                    </Select>
                </Stack>
            </Box>
            <img src={mapform} alt="mapform" width="100%"></img>
        </Box>
    );
};
