import { Avatar, AvatarGroup, Box, Grid, Stack, TextField, Typography } from "@mui/material";
import { useContext, useState } from "react";
import ProjectContext from "../context/ProjectContext";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import { stringAvatar } from "../utils/stringAvatar";
import FormatColorFillIcon from "@mui/icons-material/FormatColorFill";
// import { ProjectHouseBlockCardItem } from "./ProjectHouseBlockCardItem";
export const columnTitleStyle = {
    border: "solid 1px #ddd",
    p: 0.6,
    color: "#FFFFFF",
    backgroundColor: "#738092",
};

export const ProjectsWithHouseBlock = (props: any) => {
    const { selectedProject } = useContext(ProjectContext);
    const [projectEditable, setProjectEditable] = useState(false);
    const [openColorDialog, setOpenColorDialog] = useState(false);
    return (
        <Stack my={1} p={1} mb={10}>
            <Stack>
                {/* List project properties */}
                <Grid container my={2}>
                    <Grid
                        item
                        sm={12}
                        sx={{
                            backgroundColor: selectedProject?.projectColor,
                            color: "#FFFFFF",
                            p: 1,
                        }}
                        display="flex"
                        justifyContent="space-between"
                        alignItems="center"
                    >
                        Naam: {selectedProject?.projectName}{" "}
                        <Box sx={{ cursor: "pointer" }}>
                            <FormatColorFillIcon sx={{ mr: 2 }} onClick={() => setOpenColorDialog(true)} />
                            {!projectEditable && <EditIcon onClick={() => setProjectEditable(true)} />}
                            {projectEditable && <SaveIcon onClick={() => setProjectEditable(false)} />}
                        </Box>
                    </Grid>
                    <Grid item sm={1}>
                        <Typography sx={columnTitleStyle}>Totaal Aantal</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{selectedProject?.totalValue}</Typography>
                        ) : (
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
                    </Grid>
                    <Grid item sm={1}>
                        <Typography sx={columnTitleStyle}>Eigenaar</Typography>

                        {!projectEditable ? (
                            <AvatarGroup max={3}>
                                {selectedProject?.projectOwners.map((owner: any[]) => {
                                    return <Avatar {...stringAvatar(`${owner[2]} ${owner[3]}`)} />;
                                })}
                            </AvatarGroup>
                        ) : (
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
                    </Grid>
                    <Grid item sm={4}>
                        <Typography sx={columnTitleStyle}>Plan Type</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5, overflow: "hidden" }}>
                                {selectedProject?.planType.map((pt: string) => {
                                    return <>{pt},</>;
                                })}
                            </Typography>
                        ) : (
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
                    </Grid>
                    <Grid item sm={1}>
                        <Typography sx={columnTitleStyle}>Start Datum</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{selectedProject?.startDate}</Typography>
                        ) : (
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
                    </Grid>
                    <Grid item sm={1}>
                        <Typography sx={columnTitleStyle}>Eind Datum</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{selectedProject?.endDate}</Typography>
                        ) : (
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Priorisering</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                                {selectedProject?.priority.map((p: string) => {
                                    return <>{p},</>;
                                })}
                            </Typography>
                        ) : (
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Project Fase</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{selectedProject?.projectPhase}</Typography>
                        ) : (
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Rol Gemeente</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                                {selectedProject?.municipalityRole.map((mr: string) => {
                                    return <>{mr}</>;
                                })}
                            </Typography>
                        ) : (
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Vertrouwlijkheidsniveau</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{selectedProject?.confidentialityLevel}</Typography>
                        ) : (
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
                    </Grid>
                    <Grid item sm={1}>
                        <Typography sx={columnTitleStyle}>Project Leider</Typography>

                        {!projectEditable ? (
                            <Box sx={{ border: "solid 1px #ddd", overflow: "hidden" }}>
                                <AvatarGroup max={3}>
                                    {selectedProject?.projectLeaders.map((leader: any[]) => {
                                        return <Avatar {...stringAvatar(`${leader[2]} ${leader[3]}`)} />;
                                    })}
                                </AvatarGroup>
                            </Box>
                        ) : (
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
                    </Grid>
                    <Grid item sm={4}>
                        <Typography sx={columnTitleStyle}>Planologische Plan Status</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5, overflow: "hidden" }}>
                                {selectedProject?.planningPlanStatus.map((pp: string) => {
                                    return <>{pp}</>;
                                })}
                            </Typography>
                        ) : (
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Regio</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5, overflow: "hidden" }}>
                                {selectedProject?.municipality.map((municipality: string) => {
                                    return <>{municipality},</>;
                                })}
                            </Typography>
                        ) : (
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Buurt</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5, overflow: "hidden" }}>
                                {selectedProject?.buurt.map((buurt: string) => {
                                    return <>{buurt},</>;
                                })}
                            </Typography>
                        ) : (
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Wijk</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5, overflow: "hidden" }}>
                                {selectedProject?.wijk.map((wijk: string) => {
                                    return <>{wijk},</>;
                                })}
                            </Typography>
                        ) : (
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
                    </Grid>
                </Grid>
                {/* List huizen blok cards */}
                {/* <Grid container my={2}>
                    {houseblocks.map((hb: any, i: number) => {
                        return <ProjectHouseBlockCardItem hb={hb} key={i} />;
                    })}
                </Grid> */}
                {openColorDialog && <>Add here later color dialog</>}
            </Stack>
        </Stack>
    );
};
