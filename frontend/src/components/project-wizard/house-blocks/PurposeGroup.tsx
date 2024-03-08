import { Stack, Typography, TextField } from "@mui/material";
import { t } from "i18next";

export const PurposeGroup = (props: any) => {
    return (
        <Stack mt={1} direction="column" border="solid 1px #ddd" sx={{ backgroundColor: "#F9F9F9" }} p={2}>
            <Typography variant="subtitle1" fontWeight="600">
                {t("createProject.houseBlocksForm.goal")}
            </Typography>
            <Stack direction="row" spacing={1}>
                <Stack>
                    <Typography variant="subtitle1" fontWeight="500">
                        {t("createProject.houseBlocksForm.regular")}
                    </Typography>
                    <TextField
                        id="regular"
                        size="small"
                        variant="outlined"
                        value={props.createProjectForm ? props.createProjectForm.regulier : ""}
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                regulier: e.target.value,
                            })
                        }
                    />
                </Stack>
                <Stack>
                    <Typography variant="subtitle1" fontWeight="500">
                        {t("createProject.houseBlocksForm.youngPeople")}
                    </Typography>
                    <TextField
                        id="youngPeople"
                        size="small"
                        variant="outlined"
                        value={props.createProjectForm ? props.createProjectForm.jongeren : ""}
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                jongeren: e.target.value,
                            })
                        }
                    />
                </Stack>
                <Stack>
                    <Typography variant="subtitle1" fontWeight="500">
                        {t("createProject.houseBlocksForm.students")}
                    </Typography>
                    <TextField
                        id=" students"
                        size="small"
                        variant="outlined"
                        value={props.createProjectForm ? props.createProjectForm.sudenten : ""}
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                sudenten: e.target.value,
                            })
                        }
                    />
                </Stack>

                <Stack>
                    <Typography variant="subtitle1" fontWeight="500">
                        {t("createProject.houseBlocksForm.elderly")}
                    </Typography>
                    <TextField
                        id="elderly"
                        size="small"
                        variant="outlined"
                        value={props.createProjectForm ? props.createProjectForm.ouderen : ""}
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                ouderen: e.target.value,
                            })
                        }
                    />
                </Stack>
                <Stack>
                    <Typography variant="subtitle1" fontWeight="500">
                        {t("createProject.houseBlocksForm.disabilityAndCare")}
                    </Typography>
                    <TextField
                        id="disabilityAndCare"
                        size="small"
                        variant="outlined"
                        value={props.createProjectForm ? props.createProjectForm.gehandicapterenzorg : ""}
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                gehandicapterenzorg: e.target.value,
                            })
                        }
                    />
                </Stack>

                <Stack>
                    <Typography variant="subtitle1" fontWeight="500">
                        {t("createProject.houseBlocksForm.largeFamilies")}
                    </Typography>
                    <TextField
                        id="grotegezinnen"
                        size="small"
                        variant="outlined"
                        value={props.createProjectForm ? props.createProjectForm.grotegezinnen : ""}
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                grotegezinnen: e.target.value,
                            })
                        }
                    />
                </Stack>
            </Stack>
        </Stack>
    );
};
