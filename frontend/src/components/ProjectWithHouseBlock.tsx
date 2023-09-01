import { Grid, Stack, Typography } from "@mui/material";
import { useContext, useState } from "react";
import { ImportHouseBlockCardItem } from "./ImportHouseBlockCardItem";
import ProjectContext from "../context/ProjectContext";
import { colorArray } from "../api/dummyData";
export const columnTitleStyle = {
    border: "solid 1px #ddd",
    p: 0.6,
    color: "#FFFFFF",
    backgroundColor: "#738092",
};

export const ProjectsWithHouseBlock = (props: any) => {
    const { project, houseblocks } = props;

    const { id } = useContext(ProjectContext);
    return (
        <Stack border="solid 2px #ddd" my={1} p={1}>
            <Stack>
                {/* List project properties */}
                <Grid container my={2}>
                    <Grid
                        item
                        sm={12}
                        sx={{
                            backgroundColor: id && colorArray[parseInt(id) - 1],
                            color: "#FFFFFF",
                            p: 1,
                        }}
                        display="flex"
                        justifyContent="space-between"
                        alignItems="center"
                    >
                        Naam: {project.name}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Eigenaar</Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project.eigenaar}
                        </Typography>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Plan Type</Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project["plan type"]}
                        </Typography>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>
                            Start Datum
                        </Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project["start datum"]}
                        </Typography>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>
                            Eind Datum
                        </Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project["eind datum"]}
                        </Typography>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>
                            Priorisering
                        </Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project["priorisering"]}
                        </Typography>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>
                            Project Fase
                        </Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project["project fase"]}
                        </Typography>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>
                            Rol Gemeente
                        </Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project["rol gemeente"]}
                        </Typography>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>
                            Programmering
                        </Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project["programmering"]}
                        </Typography>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>
                            Project Leider
                        </Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project["project leider"]}
                        </Typography>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>
                            Vertrouwlijkheidsniveau
                        </Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project["vertrouwlijkheidsniveau"]}
                        </Typography>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>
                            Planologische Plan Status
                        </Typography>

                        <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project["planologische plan status"]}
                        </Typography>
                    </Grid>
                </Grid>
                {/* List huizen blok cards */}
                <Grid container my={2}>
                    {houseblocks.map((hb: any, i: number) => {
                        return <ImportHouseBlockCardItem hb={hb} key={i} />;
                    })}
                </Grid>
            </Stack>
        </Stack>
    );
};
