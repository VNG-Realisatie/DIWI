import { Stack, Typography, TextField } from "@mui/material";
import { t } from "i18next";

export const PhysicalAppeareanceGroup = (props: any) => {
    return (
        <Stack mt={1} direction="column" border="solid 1px #ddd" sx={{ backgroundColor: "#F9F9F9" }} width="100%" p={2}>
            <Typography variant="subtitle1" fontWeight="600">
                {t("createProject.houseBlocksForm.physicalApperance")}
            </Typography>
            <Stack direction="row" justifyContent="space-between">
                <Stack>
                    <Stack>
                        <Typography variant="subtitle1" fontWeight="500">
                            {t("createProject.houseBlocksForm.terracedHouse")}
                        </Typography>
                        <TextField
                            id="terracedHouse"
                            size="small"
                            variant="outlined"
                            value={props.createProjectForm ? props.createProjectForm.tussenwoning : ""}
                            onChange={(e) =>
                                props.setCreateProjectForm({
                                    ...props.createProjectForm,
                                    tussenwoning: e.target.value,
                                })
                            }
                        />
                    </Stack>
                    <Stack>
                        <Typography variant="subtitle1" fontWeight="500">
                            {t("createProject.houseBlocksForm.cornerHouse")}
                        </Typography>
                        <TextField
                            id="cornerHouse"
                            size="small"
                            variant="outlined"
                            value={props.createProjectForm ? props.createProjectForm.hoekwoning : ""}
                            onChange={(e) =>
                                props.setCreateProjectForm({
                                    ...props.createProjectForm,
                                    hoekwoning: e.target.value,
                                })
                            }
                        />
                    </Stack>
                </Stack>
                <Stack>
                    <Stack>
                        <Typography variant="subtitle1" fontWeight="500">
                            {t("createProject.houseBlocksForm.semidetached")}
                        </Typography>
                        <TextField
                            id="semidetached"
                            size="small"
                            variant="outlined"
                            value={props.createProjectForm ? props.createProjectForm.tweeondereenkap : ""}
                            onChange={(e) =>
                                props.setCreateProjectForm({
                                    ...props.createProjectForm,
                                    tweeondereenkap: e.target.value,
                                })
                            }
                        />
                    </Stack>

                    <Stack>
                        <Typography variant="subtitle1" fontWeight="500">
                            {t("createProject.houseBlocksForm.detached")}
                        </Typography>
                        <TextField
                            id="detached"
                            size="small"
                            variant="outlined"
                            value={props.createProjectForm ? props.createProjectForm.vrijstaand : ""}
                            onChange={(e) =>
                                props.setCreateProjectForm({
                                    ...props.createProjectForm,
                                    vrijstaand: e.target.value,
                                })
                            }
                        />
                    </Stack>
                </Stack>
                <Stack>
                    <Stack>
                        <Typography variant="subtitle1" fontWeight="500">
                            {t("createProject.houseBlocksForm.porticoApartment")}
                        </Typography>
                        <TextField
                            id="porticoApartment"
                            size="small"
                            variant="outlined"
                            value={props.createProjectForm ? props.createProjectForm.portiekflat : ""}
                            onChange={(e) =>
                                props.setCreateProjectForm({
                                    ...props.createProjectForm,
                                    portiekflat: e.target.value,
                                })
                            }
                        />
                    </Stack>

                    <Stack>
                        <Typography variant="subtitle1" fontWeight="500">
                            {t("createProject.houseBlocksForm.galleryFlat")}
                        </Typography>
                        <TextField
                            id="galleryFlat"
                            size="small"
                            variant="outlined"
                            value={props.createProjectForm ? props.createProjectForm.gallerijflat : ""}
                            onChange={(e) =>
                                props.setCreateProjectForm({
                                    ...props.createProjectForm,
                                    gallerijflat: e.target.value,
                                })
                            }
                        />
                    </Stack>
                </Stack>
                <Stack>
                    <Stack>
                        <Typography variant="subtitle1" fontWeight="500">
                            {t("createProject.houseBlocksForm.singleFamilyHome")}
                        </Typography>
                        <TextField
                            id="singleFamilyHome"
                            size="small"
                            variant="outlined"
                            value={props.createProjectForm ? props.createProjectForm.eengezinswoning : ""}
                            onChange={(e) =>
                                props.setCreateProjectForm({
                                    ...props.createProjectForm,
                                    eengezinswoning: e.target.value,
                                })
                            }
                        />
                    </Stack>
                    <Stack>
                        <Typography variant="subtitle1" fontWeight="500">
                            {t("createProject.houseBlocksForm.multiFamilyHome")}
                        </Typography>
                        <TextField
                            id="multiFamilyHome"
                            size="small"
                            variant="outlined"
                            value={props.createProjectForm ? props.createProjectForm.meergezinswoning : ""}
                            onChange={(e) =>
                                props.setCreateProjectForm({
                                    ...props.createProjectForm,
                                    meergezinswoning: e.target.value,
                                })
                            }
                        />
                    </Stack>
                </Stack>
            </Stack>
        </Stack>
    );
};
