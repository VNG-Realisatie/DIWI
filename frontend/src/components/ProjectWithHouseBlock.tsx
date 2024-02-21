import { Avatar, AvatarGroup, Box, Grid, IconButton, Stack, TextField, Tooltip, Typography } from "@mui/material";
import { ChangeEvent, useContext, useState } from "react";
import ProjectContext from "../context/ProjectContext";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import { stringAvatar } from "../utils/stringAvatar";
import FormatColorFillIcon from "@mui/icons-material/FormatColorFill";
import { updateProject } from "../api/projectsServices";
import CloseIcon from "@mui/icons-material/Close";
import useAlert from "../hooks/useAlert";
import { useTranslation } from "react-i18next";
// import { ProjectHouseBlockCardItem } from "./ProjectHouseBlockCardItem";
export const columnTitleStyle = {
    border: "solid 1px #ddd",
    p: 0.6,
    color: "#FFFFFF",
    backgroundColor: "#738092",
};

export const ProjectsWithHouseBlock = (props: any) => {
    const { selectedProject, id } = useContext(ProjectContext);
    const [projectEditable, setProjectEditable] = useState(false);
    const [openColorDialog, setOpenColorDialog] = useState(false);
    const [name, setName] = useState<string | undefined>();
    const [editForm, setEditForm] = useState("");
    const [totalValue, setTotalValue] = useState<string | undefined>();
    const handleNameChange = (event: ChangeEvent<HTMLInputElement>) => {
        setName(event.target.value);
    };
    const handleSaveName = () => {
        const updatedData = {
            property: "name",
            value: name,
        };
        id &&
            updateProject(id, updatedData).then((_) => {
                //Ask Daniela to support res
                setAlert("Project name updated", "success");
            });
        setEditForm("");
    };

    const { setAlert } = useAlert();
    const { t } = useTranslation();

    const handleTotalValueChange = (event: ChangeEvent<HTMLInputElement>) => {
        setTotalValue(event.target.value);
    };

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
                        {editForm !== "name" ? (
                            <Typography onClick={() => setEditForm("name")}>
                                {t("projects.tableColumns.projectName")}: {name ? name : selectedProject?.projectName}
                            </Typography>
                        ) : (
                            <Stack direction="row" alignItems="center" spacing={1}>
                                <TextField
                                    size="small"
                                    sx={{ border: "solid 1px white" }}
                                    label={t("projects.tableColumns.projectName")}
                                    value={name}
                                    onChange={handleNameChange}
                                />
                                <Tooltip title="Cancel Changes">
                                    <IconButton
                                        color="error"
                                        onClick={() => {
                                            setEditForm("");
                                            setName(selectedProject?.projectName);
                                        }}
                                    >
                                        <CloseIcon />
                                    </IconButton>
                                </Tooltip>
                                <Tooltip title="Save Changes">
                                    <IconButton color="info" onClick={handleSaveName}>
                                        <SaveIcon />
                                    </IconButton>
                                </Tooltip>
                            </Stack>
                        )}
                        <Box sx={{ cursor: "pointer" }}>
                            <FormatColorFillIcon sx={{ mr: 2 }} onClick={() => setOpenColorDialog(true)} />
                            {!projectEditable && <EditIcon onClick={() => setProjectEditable(true)} />}
                            {projectEditable && <SaveIcon onClick={() => setProjectEditable(false)} />}
                        </Box>
                    </Grid>
                    <Grid item sm={1}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.totalValue")}</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{totalValue ? totalValue : selectedProject?.totalValue}</Typography>
                        ) : (
                            <TextField value={totalValue} size="small" id="total-value" variant="outlined" onChange={handleTotalValueChange} />
                        )}
                    </Grid>
                    <Grid item sm={1}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.organizationName")}</Typography>

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
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.planType")}</Typography>

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
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.startDate")}</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{selectedProject?.startDate}</Typography>
                        ) : (
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
                    </Grid>
                    <Grid item sm={1}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.endDate")}</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{selectedProject?.endDate}</Typography>
                        ) : (
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.priority")}</Typography>

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
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.projectPhase")}</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{selectedProject?.projectPhase}</Typography>
                        ) : (
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.municipalityRole")}</Typography>

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
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.confidentialityLevel")}</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{selectedProject?.confidentialityLevel}</Typography>
                        ) : (
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
                    </Grid>
                    <Grid item sm={1}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.projectLeader")}</Typography>

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
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.planningPlanStatus")}</Typography>

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
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.municipality")}</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5, overflow: "hidden" }}>
                                {selectedProject?.municipality?.map((municipality: string) => {
                                    return <>{municipality},</>;
                                })}
                            </Typography>
                        ) : (
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.buurt")}</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5, overflow: "hidden" }}>
                                {selectedProject?.buurt?.map((buurt: string) => {
                                    return <>{buurt},</>;
                                })}
                            </Typography>
                        ) : (
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.wijk")}</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5, overflow: "hidden" }}>
                                {selectedProject?.wijk?.map((wijk: string) => {
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
