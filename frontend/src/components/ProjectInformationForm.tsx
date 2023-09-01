import {
    Box,
    InputLabel,
    MenuItem,
    Select,
    Stack,
    TextField,
    Typography,
} from "@mui/material";
import ColorSelector from "./ColorSelector";
import { DatePicker } from "@mui/x-date-pickers";

import rolMunicipality from "../api/json/gemeente_rol.json";
import projectLead from "../api/json/projectleider.json";
import projectFaseList from "../api/json/enums/project_phase.json";
import planologischePlanStatus from "../api/json/enums/planologische_planstatus.json";
import vertrouwlijkheidsniveau from "../api/json/enums/confidentiality.json";

export const ProjectInformationForm = (props: any) => {
    //ToDo add props later
    const handleColorChange = (newColor: string) => {
        console.log("Selected color:", newColor);
        // You can perform any action with the selected color here
        props.setCreateProjectForm({
            ...props.createProjectForm,
            color: newColor,
        });
    };
    return (
        <Box mt={4}>
            <Typography variant="h6" fontWeight="600">
                Vul de projectgegevens in
            </Typography>
            <Typography variant="subtitle1" fontWeight="500">
                Projectnaam en projectkleur*
            </Typography>
            <Stack
                direction="row"
                alignItems="center"
                justifyContent="space-between"
            >
                <TextField
                    id="projectname"
                    size="small"
                    variant="outlined"
                    value={
                        props.createProjectForm
                            ? props.createProjectForm.name
                            : ""
                    }
                    sx={{ width: "97%" }}
                    onChange={(e) =>
                        props.setCreateProjectForm({
                            ...props.createProjectForm,
                            name: e.target.value,
                        })
                    }
                />
                <ColorSelector
                    selectedColor={props.createProjectForm}
                    defaultColor="rgba(255, 87, 51, 1)"
                    onColorChange={handleColorChange}
                />
            </Stack>
            <Typography variant="subtitle1" fontWeight="500">
                Eigenaar
            </Typography>
            <TextField
                id="eigenaar"
                size="small"
                variant="outlined"
                value={
                    props.createProjectForm
                        ? props.createProjectForm.eigenaar
                        : ""
                }
                onChange={(e) =>
                    props.setCreateProjectForm({
                        ...props.createProjectForm,
                        eigenaar: e.target.value,
                    })
                }
                fullWidth
            />
            <Typography variant="subtitle1" fontWeight="500">
                Plan Type
            </Typography>
            <TextField
                id="plantype"
                size="small"
                variant="outlined"
                value={
                    props.createProjectForm
                        ? props.createProjectForm["plan type"]
                        : ""
                }
                onChange={(e) =>
                    props.setCreateProjectForm({
                        ...props.createProjectForm,
                        "plan type": e.target.value,
                    })
                }
                fullWidth
            />
            <Stack
                direction="row"
                justifyContent="flex-start"
                alignItems="center"
            >
                <Stack>
                    <Typography variant="subtitle1" fontWeight="500">
                        Start Datum
                    </Typography>
                    <DatePicker
                        value={
                            props.createProjectForm
                                ? props.createProjectForm["start datum"]
                                : ""
                        }
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                "start datum": e,
                            })
                        }
                    />
                </Stack>
                <Stack>
                    <Typography variant="subtitle1" fontWeight="500">
                        Eind Datum
                    </Typography>
                    <DatePicker
                        value={
                            props.createProjectForm
                                ? props.createProjectForm["eind datum"]
                                : ""
                        }
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                "eind datum": e,
                            })
                        }
                    />
                </Stack>
            </Stack>
            <Stack direction="row" justifyContent="space-between" flexWrap="wrap">
                <Stack>
                    <InputLabel id="priority">Priorisering</InputLabel>
                    <Select
                        sx={{ width: "370px" }}
                        labelId="priority"
                        id="prio"
                        value={
                            props.createProjectForm
                                ? props.createProjectForm.priorisering
                                : ""
                        }
                        label="Priorisering"
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                priorisering: e.target.value,
                            })
                        }
                    >
                        <MenuItem value="laag">Laag</MenuItem>
                        <MenuItem value="gemiddeld">Gemiddeld</MenuItem>
                        <MenuItem value="hoog">Hoog</MenuItem>
                    </Select>
                </Stack>
                <Stack>
                    <InputLabel id="projectfase">Project Fase</InputLabel>
                    <Select
                        sx={{ width: "370px" }}
                        labelId="projectfase"
                        id="fase"
                        value={
                            props.createProjectForm
                                ? props.createProjectForm["project fase"]
                                : ""
                        }
                        label="Project Fase"
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                "project fase": e.target.value,
                            })
                        }
                    >
                        {projectFaseList.map((fase) => {
                            return <MenuItem key={fase} value={fase}>{fase}</MenuItem>;
                        })}
                    </Select>
                </Stack>
                <Stack>
                    <InputLabel id="rol">Rol Gemeente</InputLabel>
                    <Select
                        sx={{ width: "370px" }}
                        labelId="rol"
                        id="gemeenterol"
                        value={
                            props.createProjectForm
                                ? props.createProjectForm["rol gemeente"]
                                : ""
                        }
                        label="Rol Gemeente"
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                "rol gemeente": e.target.value,
                            })
                        }
                    >
                         {rolMunicipality.map((municipality) => {
                            return <MenuItem key={municipality.ID} value={municipality.value_label}>{municipality.value_label}</MenuItem>;
                        })}
                    </Select>
                </Stack>
                <Stack>
                    <InputLabel id="pro">Programmering</InputLabel>
                    <Select
                        sx={{ width: "370px" }}
                        labelId="pro"
                        id="programming"
                        value={
                            props.createProjectForm
                                ? props.createProjectForm.programmering
                                : ""
                        }
                        label="Programmering"
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                programmering: e.target.value,
                            })
                        }
                    >
                        <MenuItem value="true">Ja</MenuItem>
                        <MenuItem value="false">Nee</MenuItem>
                    </Select>
                </Stack>
                <Stack>
                    <InputLabel id="leider">Project Leider</InputLabel>
                    <Select
                        sx={{ width: "370px" }}
                        labelId="leider"
                        id="proleider"
                        value={
                            props.createProjectForm
                                ? props.createProjectForm["project leider"]
                                : ""
                        }
                        label="Project Leider"
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                "project leider": e.target.value,
                            })
                        }
                    >
                        {projectLead.map((lead) => {
                            return <MenuItem key={lead.ID} value={lead.name}>{lead.name}</MenuItem>;
                        })}
                    </Select>
                </Stack>
                <Stack>
                    <InputLabel id="vertrouwlijkheidsniveau">Vertrouwlijkheidsniveau</InputLabel>
                    <Select
                        sx={{ width: "370px" }}
                        labelId="vertrouwlijkheidsniveau"
                        id="i-vertrouwlijkheidsniveau"
                        value={
                            props.createProjectForm
                                ? props.createProjectForm.vertrouwlijkheidsniveau
                                : ""
                        }
                        label="Vertrouwlijkheidsniveau"
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                vertrouwlijkheidsniveau: e.target.value,
                            })
                        }
                    >
                        {vertrouwlijkheidsniveau.map((v) => {
                            return <MenuItem key={v} value={v}>{v}</MenuItem>;
                        })}
                    </Select>
                </Stack>
                <Stack>
                    <InputLabel id="planologischePlanStatus">Planologische Plan Status</InputLabel>
                    <Select
                        sx={{ width: "370px" }}
                        labelId="planologischePlanStatus"
                        id="planologische-plan-status"
                        value={
                            props.createProjectForm
                                ? props.createProjectForm["planologische plan status"]
                                : ""
                        }
                        label="Vertrouwlijkheidsniveau"
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                "planologische plan status": e.target.value,
                            })
                        }
                    >
                        {planologischePlanStatus.map((v) => {
                            return <MenuItem key={v} value={v}>{v}</MenuItem>;
                        })}
                    </Select>
                </Stack>
            </Stack>
        </Box>
    );
};
