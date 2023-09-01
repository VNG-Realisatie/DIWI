import { Box, Grid, Stack, TextField, Typography } from "@mui/material";
import { useContext, useState } from "react";
import ProjectContext from "../context/ProjectContext";
import { colorArray } from "../api/dummyData";
import EditIcon from '@mui/icons-material/Edit';
import SaveIcon from '@mui/icons-material/Save';
import { ProjectHouseBlockCardItem } from "./ProjectHouseBlockCardItem";
export const columnTitleStyle = {
    border: "solid 1px #ddd",
    p: 0.6,
    color: "#FFFFFF",
    backgroundColor: "#738092",
};

export const ProjectsWithHouseBlock = (props: any) => {
    const { project, houseblocks } = props;
    const[projectEditable,setProjectEditable]=useState(false);

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
                        Naam: {project.name}  <Box sx={{cursor:"pointer"}} >{!projectEditable&&<EditIcon onClick={()=>setProjectEditable(true)}/>}{projectEditable&&<SaveIcon onClick={()=>setProjectEditable(false)}/>}</Box>
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Eigenaar</Typography>

                       {!projectEditable? <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project.eigenaar}
                        </Typography>:<TextField size="small" id="outlined-basic"  variant="outlined" />}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>Plan Type</Typography>

                        {!projectEditable?<Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project["plan type"]}
                        </Typography>:<TextField size="small" id="outlined-basic" variant="outlined" />}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>
                            Start Datum
                        </Typography>

                        {!projectEditable?<Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project["start datum"]}
                        </Typography>:<TextField size="small" id="outlined-basic" variant="outlined" />}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>
                            Eind Datum
                        </Typography>

                        {!projectEditable?  <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project["eind datum"]}
                        </Typography>:<TextField size="small" id="outlined-basic"  variant="outlined" />}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>
                            Priorisering
                        </Typography>

                        {!projectEditable? <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project["priorisering"]}
                        </Typography>:<TextField size="small" id="outlined-basic"  variant="outlined" />}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>
                            Project Fase
                        </Typography>

                        {!projectEditable?  <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project["project fase"]}
                        </Typography>:<TextField size="small" id="outlined-basic"  variant="outlined" />}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>
                            Rol Gemeente
                        </Typography>

                        {!projectEditable?  <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project["rol gemeente"]}
                        </Typography>:<TextField size="small" id="outlined-basic"  variant="outlined" />}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>
                            Programmering
                        </Typography>

                        {!projectEditable?  <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project["programmering"]}
                        </Typography>:<TextField size="small" id="outlined-basic"  variant="outlined" />}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>
                            Project Leider
                        </Typography>

                        {!projectEditable?   <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project["project leider"]}
                        </Typography>:<TextField size="small" id="outlined-basic"  variant="outlined" />}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>
                            Vertrouwlijkheidsniveau
                        </Typography>

                        {!projectEditable?  <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project["vertrouwlijkheidsniveau"]}
                        </Typography>:<TextField size="small" id="outlined-basic"  variant="outlined" />}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>
                            Planologische Plan Status
                        </Typography>

                        {!projectEditable?   <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                            {project["planologische plan status"]}
                        </Typography>:<TextField size="small" id="outlined-basic"  variant="outlined" />}
                    </Grid>
                </Grid>
                {/* List huizen blok cards */}
                <Grid container my={2}>
                    {houseblocks.map((hb: any, i: number) => {
                        return <ProjectHouseBlockCardItem hb={hb} key={i} />;
                    })}
                </Grid>
            </Stack>
        </Stack>
    );
};
