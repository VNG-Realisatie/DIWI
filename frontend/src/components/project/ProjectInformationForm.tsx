import { Box, InputLabel, MenuItem, Select, Stack, TextField, Typography } from "@mui/material";
import ColorSelector from "../ColorSelector";
import { DatePicker } from "@mui/x-date-pickers";

import rolMunicipality from "../../api/json/gemeente_rol.json";
import projectLead from "../../api/json/projectleider.json";
import projectFaseList from "../../api/json/enums/project_phase.json";
import planologischePlanStatus from "../../api/json/enums/planologische_planstatus.json";
import vertrouwlijkheidsniveau from "../../api/json/enums/confidentiality.json";
import priorityOption from "../../api/json/priorisering.json";
import eigenaarOption from "../../api/json/eigenaar.json";
import { useEffect } from "react";
import { useTranslation } from "react-i18next";

export const ProjectInformationForm = ({ setCreateProjectForm, createProjectForm }: any) => {
    const { t } = useTranslation();
    //ToDo add props later
    const handleColorChange = (newColor: string) => {
        setCreateProjectForm({
            ...createProjectForm,
            color: newColor,
        });
    };

    useEffect(() => {
        setCreateProjectForm({
            ...createProjectForm,
            id: Math.floor(Math.random() * 10000),
        });
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return (
        <Box mt={4}>
            <Typography variant="h6" fontWeight="600">
                {t("createProject.informationForm.title")}
            </Typography>
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.informationForm.nameLabel")}
            </Typography>
            <Stack direction="row" alignItems="center" justifyContent="space-between">
                <TextField
                    id="projectname"
                    size="small"
                    variant="outlined"
                    value={createProjectForm ? createProjectForm.name : ""}
                    sx={{ width: "97%" }}
                    onChange={(e) =>
                        setCreateProjectForm({
                            ...createProjectForm,
                            name: e.target.value,
                        })
                    }
                />
                <ColorSelector selectedColor={createProjectForm} defaultColor="rgba(255, 87, 51, 1)" onColorChange={handleColorChange} />
            </Stack>

            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.informationForm.planType")}
            </Typography>
            <TextField
                id="plantype"
                size="small"
                variant="outlined"
                value={createProjectForm ? createProjectForm["plan type"] : ""}
                onChange={(e) =>
                    setCreateProjectForm({
                        ...createProjectForm,
                        "plan type": e.target.value,
                    })
                }
                fullWidth
            />

            <Stack direction="row" justifyContent="space-between" flexWrap="wrap" gap="15px" mt="10px">
                <Stack direction="row" justifyContent="flex-start" alignItems="center">
                    <Stack>
                        <Typography variant="subtitle1" fontWeight="500">
                            {t("createProject.informationForm.startDate")}
                        </Typography>
                        <DatePicker
                            sx={{ width: "185px" }}
                            value={createProjectForm ? createProjectForm["start datum"] : ""}
                            onChange={(e) =>
                                setCreateProjectForm({
                                    ...createProjectForm,
                                    "start datum": e,
                                })
                            }
                        />
                    </Stack>
                    <Stack>
                        <Typography variant="subtitle1" fontWeight="500">
                            {t("createProject.informationForm.endDate")}{" "}
                        </Typography>
                        <DatePicker
                            sx={{ width: "185px" }}
                            value={createProjectForm ? createProjectForm["eind datum"] : ""}
                            onChange={(e) =>
                                setCreateProjectForm({
                                    ...createProjectForm,
                                    "eind datum": e,
                                })
                            }
                        />
                    </Stack>
                </Stack>
                <Stack>
                    <InputLabel id="priority">{t("createProject.informationForm.priority")}</InputLabel>
                    <Select
                        sx={{ width: "370px" }}
                        labelId="priority"
                        id="prio"
                        value={createProjectForm ? createProjectForm.priorisering : ""}
                        label={t("createProject.informationForm.priority")}
                        onChange={(e) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                priorisering: e.target.value,
                            })
                        }
                    >
                        {priorityOption.map((v) => {
                            return (
                                <MenuItem key={v.ID} value={v.value_label}>
                                    {v.value_label}
                                </MenuItem>
                            );
                        })}
                    </Select>
                </Stack>
                <Stack>
                    <InputLabel id="projectPhase">{t("createProject.informationForm.projectPhase")}</InputLabel>
                    <Select
                        sx={{ width: "370px" }}
                        labelId="projectPhase"
                        id="fase"
                        value={createProjectForm ? createProjectForm["project fase"] : ""}
                        label={t("createProject.informationForm.projectPhase")}
                        onChange={(e) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                "project fase": e.target.value,
                            })
                        }
                    >
                        {projectFaseList.map((fase) => {
                            return (
                                <MenuItem key={fase} value={fase}>
                                    {fase}
                                </MenuItem>
                            );
                        })}
                    </Select>
                </Stack>
                <Stack>
                    <InputLabel id="role">{t("createProject.informationForm.roleMunicipality")}</InputLabel>
                    <Select
                        sx={{ width: "370px" }}
                        labelId="role"
                        id="roleMunicipality"
                        value={createProjectForm ? createProjectForm["rol gemeente"] : ""}
                        label={t("createProject.informationForm.roleMunicipality")}
                        onChange={(e) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                "rol gemeente": e.target.value,
                            })
                        }
                    >
                        {rolMunicipality.map((municipality) => {
                            return (
                                <MenuItem key={municipality.ID} value={municipality.value_label}>
                                    {municipality.value_label}
                                </MenuItem>
                            );
                        })}
                    </Select>
                </Stack>
                <Stack>
                    <InputLabel id="pro">{t("createProject.informationForm.programming")}</InputLabel>
                    <Select
                        sx={{ width: "370px" }}
                        labelId="pro"
                        id="programming"
                        value={createProjectForm ? createProjectForm.programmering : ""}
                        label={t("createProject.informationForm.programming")}
                        onChange={(e) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                programmering: e.target.value,
                            })
                        }
                    >
                        <MenuItem value="true">{t("generic.yes")}</MenuItem>
                        <MenuItem value="false">{t("generic.no")}</MenuItem>
                    </Select>
                </Stack>
                <Stack>
                    <InputLabel id="projectLeader">{t("createProject.informationForm.projectLeader")}</InputLabel>
                    <Select
                        sx={{ width: "370px" }}
                        labelId="projectLeader"
                        id="project-leader"
                        value={createProjectForm ? createProjectForm["project leider"] : ""}
                        label={t("createProject.informationForm.projectLeader")}
                        onChange={(e) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                "project leider": e.target.value,
                            })
                        }
                    >
                        {projectLead.map((lead) => {
                            return (
                                <MenuItem key={lead.ID} value={lead.name}>
                                    {lead.name}
                                </MenuItem>
                            );
                        })}
                    </Select>
                </Stack>
                <Stack>
                    <InputLabel id="confidentialityLevel">{t("createProject.informationForm.confidentialityLevel")}</InputLabel>
                    <Select
                        sx={{ width: "370px" }}
                        labelId="confidentialityLevel"
                        id="i-confidentialityLevel"
                        value={createProjectForm ? createProjectForm.vertrouwlijkheidsniveau : ""}
                        label={t("createProject.informationForm.confidentialityLevel")}
                        onChange={(e) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                vertrouwlijkheidsniveau: e.target.value,
                            })
                        }
                    >
                        {vertrouwlijkheidsniveau.map((v) => {
                            return (
                                <MenuItem key={v} value={v}>
                                    {v}
                                </MenuItem>
                            );
                        })}
                    </Select>
                </Stack>
                <Stack>
                    <InputLabel id="planningPlanStatus">{t("createProject.informationForm.planningPlanStatus")}</InputLabel>
                    <Select
                        sx={{ width: "370px" }}
                        labelId="planningPlanStatus"
                        id="planning-plan-status"
                        value={createProjectForm ? createProjectForm["planologische plan status"] : ""}
                        label={t("createProject.informationForm.planningPlanStatus")}
                        onChange={(e) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                "planologische plan status": e.target.value,
                            })
                        }
                    >
                        {planologischePlanStatus.map((v) => {
                            return (
                                <MenuItem key={v} value={v}>
                                    {v}
                                </MenuItem>
                            );
                        })}
                    </Select>
                </Stack>
                <Stack>
                    <InputLabel id="leader">{t("createProject.informationForm.owner")}</InputLabel>
                    <Select
                        sx={{ width: "370px" }}
                        labelId="leader"
                        id="owner"
                        value={createProjectForm ? createProjectForm["eigenaar"] : ""}
                        label={t("createProject.informationForm.owner")}
                        onChange={(e) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                eigenaar: e.target.value,
                            })
                        }
                    >
                        {eigenaarOption.map((lead) => {
                            return (
                                <MenuItem key={lead.ID} value={lead.naam}>
                                    {lead.naam}
                                </MenuItem>
                            );
                        })}
                    </Select>
                </Stack>
            </Stack>
        </Box>
    );
};
