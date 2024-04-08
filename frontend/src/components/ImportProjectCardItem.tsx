import { Autocomplete, Checkbox, FormControl, FormControlLabel, Grid, Radio, RadioGroup, Stack, TextField, Typography } from "@mui/material";
import { useState } from "react";
import { ImportHouseBlockCardItem } from "./ImportHouseBlockCardItem";
import { SearchItem, projects } from "../api/dummyData";
import { HouseBlock } from "./project-wizard/house-blocks/types";
export const columnTitleStyle = {
    border: "solid 1px #ddd",
    p: 0.6,
    color: "#FFFFFF",
    backgroundColor: "#738092",
};
export const ImportProjectCardItem = (props: any) => {
    const dummyProjects = projects;
    //ToDo Update props type after data defined

    const [selectedOverwriteProject, setSelectedOverwriteProject] = useState<SearchItem>();
    const { project, selectedProject, setSelectedProject, overwriteProjectId, setOverwriteProjectId, projectsType, setProjectsType } = props;
    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const clearedData = projectsType.filter((o: any) => o.id !== project.id);
        clearedData.push({
            id: project.id,
            status: (event.target as HTMLInputElement).value,
        });
        setProjectsType(clearedData);
    };
    const [checked, setChecked] = useState(false);

    const handleSelectedProject = (event: React.ChangeEvent<HTMLInputElement>) => {
        setChecked(event.target.checked);
        //Store selected project ids for import endpoint
        if (event.target.checked) {
            setSelectedProject([...selectedProject, project.id]);
        } else {
            const deselectedProjectList = selectedProject.filter((s: any) => s !== project.id);
            setSelectedProject(deselectedProjectList);
        }
    };

    const selectedType = projectsType.find((o: any) => o.id === project.id).status;

    return (
        <Stack border="solid 2px #ddd" my={1} p={1}>
            <Stack spacing={2} direction="row" justifyContent="flex-end">
                <FormControl>
                    <RadioGroup
                        sx={{
                            display: "flex",
                            flexDirection: "row",
                            justifyContent: "flex-end",
                        }}
                        aria-labelledby="demo-controlled-radio-buttons-group"
                        name="controlled-radio-buttons-group"
                        value={selectedType}
                        onChange={handleChange}
                    >
                        <FormControlLabel value="new" control={<Radio size="small" />} label="Nieuw project" />
                        <FormControlLabel value="copy" control={<Radio size="small" />} label="Koppel aan:" />
                    </RadioGroup>
                </FormControl>

                <Autocomplete
                    disabled={projectsType.find((o: any) => o.id === project.id).status === "new" || !selectedProject.includes(project.id)}
                    sx={{ width: "200px" }}
                    size="small"
                    options={dummyProjects}
                    getOptionLabel={(option: SearchItem) => (option ? option.name : "")}
                    value={selectedOverwriteProject !== undefined ? selectedOverwriteProject : null}
                    onChange={(event: any, newValue: SearchItem) => {
                        setSelectedOverwriteProject(newValue);

                        if (newValue) {
                            const clearedData = overwriteProjectId.filter((o: any) => o.projectId !== project.id);
                            clearedData.push({
                                projectId: project.id,
                                willBeOverWrittenId: newValue.id,
                            });
                            setOverwriteProjectId(clearedData);
                        } else {
                            const clearedData = overwriteProjectId.filter((o: any) => o.projectId !== project.id);
                            setOverwriteProjectId(clearedData);
                        }
                    }}
                    renderInput={(params) => <TextField {...params} label="Selecteer een project" />}
                />
            </Stack>
            <Stack>
                {/* List project properties */}
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
                        alignItems="center"
                    >
                        Naam: {project.name}
                        <Checkbox checked={checked} onChange={handleSelectedProject} inputProps={{ "aria-label": "controlled" }} />
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Eigenaar</Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{project.eigenaar}</Typography>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Plan Type</Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{project["plan type"]}</Typography>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Start Datum</Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{project["start datum"]}</Typography>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Eind Datum</Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{project["eind datum"]}</Typography>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Priorisering</Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{project["priorisering"]}</Typography>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Project Fase</Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{project["project fase"]}</Typography>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Rol Gemeente</Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{project["rol gemeente"]}</Typography>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Programmering</Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{project["programmering"]}</Typography>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Project Leider</Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{project["project leider"]}</Typography>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Vertrouwlijkheidsniveau</Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{project["vertrouwlijkheidsniveau"]}</Typography>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Planologische Plan Status</Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{project["planologische plan status"]}</Typography>
                    </Grid>
                </Grid>
                {/* List huizen blok cards */}
                <Grid container my={2}>
                    {project.houseblocks.map((hb: HouseBlock) => {
                        return <ImportHouseBlockCardItem isImportCard hb={hb} key={hb.houseblockId} />;
                    })}
                </Grid>
            </Stack>
        </Stack>
    );
};
