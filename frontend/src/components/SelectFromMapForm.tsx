import { Box, InputLabel, MenuItem, Select, Stack, Typography } from "@mui/material";
import mapform from "../assets/temp/formmap.png";
import wijk from "../api/json/wijk.json";
import buurt from "../api/json/buurt.json";
import gemeente from "../api/json/gemeente.json";
import { useTranslation } from "react-i18next";

export const SelectFromMapForm = (props: any) => {
    const { t } = useTranslation();
    return (
        <Box mt={4} position="relative">
            <Typography variant="h6" fontWeight="600">
                {t("createProject.selectMapForm.title")}
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
                    <InputLabel id="municipality"> {t("createProject.selectMapForm.municipality")}</InputLabel>
                    <Select
                        sx={{ width: "100%" }}
                        labelId="municipality"
                        id="municipality"
                        value={props.createProjectForm ? props.createProjectForm.gemeente : ""}
                        label={t("createProject.selectMapForm.municipality")}
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
                    <InputLabel id="neighbourhood">{t("createProject.selectMapForm.neighbourhood")}</InputLabel>
                    <Select
                        sx={{ width: "100%" }}
                        labelId="neighbourhood"
                        id="neighbourhood"
                        value={props.createProjectForm ? props.createProjectForm.wijk : ""}
                        label={t("createProject.selectMapForm.neighbourhood")}
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
                <Stack>
                    <InputLabel id="district">{t("createProject.selectMapForm.district")}</InputLabel>
                    <Select
                        sx={{ width: "100%" }}
                        labelId="district"
                        id="district"
                        value={props.createProjectForm ? props.createProjectForm.buurt : ""}
                        label={t("createProject.selectMapForm.district")}
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
            </Box>
            <img src={mapform} alt="mapform" width="100%"></img>
        </Box>
    );
};
