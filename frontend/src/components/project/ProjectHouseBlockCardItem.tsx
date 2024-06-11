import { Box, Grid, TextField, Typography } from "@mui/material";
import { columnTitleStyle } from "../ImportProjectCardItem";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import { useState } from "react";
import useAllowedActions from "../../hooks/useAllowedActions";

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const ProjectHouseBlockCardItem = (props: any) => {
    const { hb } = props;
    const [editable, setEditable] = useState(false);
    const allowedActions = useAllowedActions();

    return (
        <>
            <Grid
                item
                sm={12}
                sx={{
                    backgroundColor: "#00A9F3",
                    color: "#FFFFFF",
                    p: 1,
                }}
                display="flex"
                justifyContent="space-between"
            >
                {hb.naam ? hb.naam : "Geen Naam"}
                {
                    <Box sx={{ cursor: "pointer" }}>
                        {editable && allowedActions.includes("EDIT_OWN_PROJECTS") ? (
                            <SaveIcon onClick={() => setEditable(false)} />
                        ) : (
                            <EditIcon onClick={() => setEditable(true)} />
                        )}
                    </Box>
                }
            </Grid>
            <Grid item sm={2}>
                <Typography sx={columnTitleStyle}>StartDatum</Typography>

                {!editable ? (
                    <Typography
                        sx={{
                            border: "solid 1px #ddd",
                            p: 0.5,
                        }}
                    >
                        {hb["start datum"]}
                    </Typography>
                ) : (
                    <TextField size="small" id="outlined-basic" variant="outlined" />
                )}
            </Grid>
            <Grid item sm={2}>
                <Typography sx={columnTitleStyle}>EindDatum</Typography>

                {!editable ? (
                    <Typography
                        sx={{
                            border: "solid 1px #ddd",
                            p: 0.5,
                        }}
                    >
                        {hb["eind datum"]}
                    </Typography>
                ) : (
                    <TextField size="small" id="outlined-basic" variant="outlined" />
                )}
            </Grid>
            <Grid item sm={2}>
                <Typography sx={columnTitleStyle}>Mutatiesoort</Typography>

                {!editable ? (
                    <Typography
                        sx={{
                            border: "solid 1px #ddd",
                            p: 0.5,
                        }}
                    >
                        {hb["mutatie_soort"]}
                    </Typography>
                ) : (
                    <TextField size="small" id="outlined-basic" variant="outlined" />
                )}
            </Grid>
            <Grid item sm={2}>
                <Typography sx={columnTitleStyle}>Bruto plancapaciteit</Typography>

                {!editable ? (
                    <Typography
                        sx={{
                            border: "solid 1px #ddd",
                            p: 0.5,
                        }}
                    >
                        {hb["bruto_plancapaciteit"]}
                    </Typography>
                ) : (
                    <TextField size="small" id="outlined-basic" variant="outlined" />
                )}
            </Grid>
            <Grid item sm={2}>
                <Typography sx={columnTitleStyle}>Sloop</Typography>

                {!editable ? (
                    <Typography
                        sx={{
                            border: "solid 1px #ddd",
                            p: 0.5,
                        }}
                    >
                        {hb.sloop}
                    </Typography>
                ) : (
                    <TextField size="small" id="outlined-basic" variant="outlined" />
                )}
            </Grid>
            <Grid item sm={2}>
                <Typography sx={columnTitleStyle}>Netto Plancapaciteit</Typography>

                {!editable ? (
                    <Typography
                        sx={{
                            border: "solid 1px #ddd",
                            p: 0.5,
                        }}
                    >
                        {hb["netto_plancapaciteit"]}
                    </Typography>
                ) : (
                    <TextField size="small" id="outlined-basic" variant="outlined" />
                )}
            </Grid>
            <Grid item sm={2}>
                <Typography sx={columnTitleStyle}>Grootte</Typography>

                {!editable ? (
                    <Typography
                        sx={{
                            border: "solid 1px #ddd",
                            p: 0.5,
                        }}
                    >
                        {hb.grootte}
                    </Typography>
                ) : (
                    <TextField size="small" id="outlined-basic" variant="outlined" />
                )}
            </Grid>
            <Grid item sm={2}>
                <Typography sx={columnTitleStyle}>Buurt</Typography>

                {!editable ? (
                    <Typography
                        sx={{
                            border: "solid 1px #ddd",
                            p: 0.5,
                        }}
                    >
                        {hb.buurt}
                    </Typography>
                ) : (
                    <TextField size="small" id="outlined-basic" variant="outlined" />
                )}
            </Grid>
            <Grid item sm={3}>
                <Typography sx={columnTitleStyle}>Wijk</Typography>

                {!editable ? (
                    <Typography
                        sx={{
                            border: "solid 1px #ddd",
                            p: 0.5,
                        }}
                    >
                        {hb.wijk}
                    </Typography>
                ) : (
                    <TextField fullWidth size="small" id="outlined-basic" variant="outlined" />
                )}
            </Grid>
            <Grid item sm={3}>
                <Typography sx={columnTitleStyle}>Fysiek Voorkomen</Typography>

                {!editable ? (
                    <Typography
                        sx={{
                            border: "solid 1px #ddd",
                            p: 0.5,
                        }}
                    >
                        {hb.fysiek_voorkomen}
                    </Typography>
                ) : (
                    <TextField fullWidth size="small" id="outlined-basic" variant="outlined" />
                )}
            </Grid>
            <Grid item sm={2}>
                <Typography sx={columnTitleStyle}>Waarde</Typography>

                {!editable ? (
                    <Typography
                        sx={{
                            border: "solid 1px #ddd",
                            p: 0.5,
                        }}
                    >
                        {hb.waarde}
                    </Typography>
                ) : (
                    <TextField size="small" id="outlined-basic" variant="outlined" />
                )}
            </Grid>
            <Grid item sm={2}>
                <Typography sx={columnTitleStyle}>Huurbedrag</Typography>

                {!editable ? (
                    <Typography
                        sx={{
                            border: "solid 1px #ddd",
                            p: 0.5,
                        }}
                    >
                        {hb.huurbedrag}
                    </Typography>
                ) : (
                    <TextField size="small" id="outlined-basic" variant="outlined" />
                )}
            </Grid>
            <Grid item sm={2} mb={6}>
                <Typography sx={columnTitleStyle}>Woningtype:</Typography>

                {!editable ? (
                    <Typography
                        sx={{
                            border: "solid 1px #ddd",
                            p: 0.5,
                        }}
                    >
                        {hb.grondpositie}
                    </Typography>
                ) : (
                    <TextField size="small" id="outlined-basic" variant="outlined" />
                )}
            </Grid>
        </>
    );
};
