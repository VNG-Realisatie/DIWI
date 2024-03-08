import { Box, Grid, InputAdornment, InputLabel, MenuItem, Select, Stack, TextField, Typography } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import mutatiesoort from "../api/json/enums/mutatie_soort.json";
import { useTranslation } from "react-i18next";
import { HouseBlock, MutationInformations, OwnershipSingleValue } from "./project-wizard/house-blocks/types";
import { PurposeGroup } from "./project-wizard/house-blocks/PurposeGroup";
import { PhysicalAppeareanceGroup } from "./project-wizard/house-blocks/PhysicalAppeareanceGroup";
import { GeneralInformationGroup } from "./project-wizard/house-blocks/general-information/GeneralInformationGroup";
import { MutationInformationGroup } from "./project-wizard/house-blocks/mutation-information/MutationInformationGroup";
import { useState } from "react";
import { OwnershipInformationGroup } from "./project-wizard/house-blocks/ownership-information/OwnershipInformationGroup";

type Props = {
    projectForm: HouseBlock;
    setProjectForm(project: HouseBlock): void;
};
export const BlockHousesForm = ({ projectForm, setProjectForm }: Props) => {
    const [mutationInformationForm, setMutationInformationForm] = useState<MutationInformations>({
        mutationKind: [],
        grossPlanCapacity: 0,
        netPlanCapacity: 0,
        demolition: 0,
    });
    const [ownershipInformationForm, setOwershipInformationForm] = useState<OwnershipSingleValue[]>([]);
    const { t } = useTranslation();
    return (
        <Box mt={4}>
            <Grid container spacing={2} alignItems="stretch">
                <Grid item xs={12} md={8}>
                    <GeneralInformationGroup projectForm={projectForm} setProjectForm={setProjectForm} />
                </Grid>
                <Grid item xs={12} md={4}>
                    <MutationInformationGroup projectForm={mutationInformationForm} setProjectForm={setMutationInformationForm} />
                </Grid>
            </Grid>
            <Grid container mt={2}>
                <Grid item xs={12}>
                    <OwnershipInformationGroup projectForm={ownershipInformationForm} setProjectForm={setOwershipInformationForm} />
                </Grid>
            </Grid>

            {/*
            <Stack direction="row" alignItems="flex-end" justifyContent="space-between">
                <Stack direction="column" width="49%" border="solid 1px #ddd" sx={{ backgroundColor: "#F9F9F9" }} p={2}>
                    <Stack>
                        <Typography variant="subtitle1" fontWeight="600">
                            {t("createProject.houseBlocksForm.ownershipAndValue")}
                        </Typography>
                    </Stack>
                    <Stack direction="row" alignItems="flex-start" justifyContent="space-between">
                        <Stack width="49%">
                            <Stack>
                                <Typography variant="subtitle1" fontWeight="500">
                                    {t("createProject.houseBlocksForm.ownerOccupiedHome")}
                                </Typography>
                                <TextField
                                    id="ownerOccupiedHome"
                                    size="small"
                                    variant="outlined"
                                    value={props.createProjectForm ? props.createProjectForm.eigendom_soort : ""}
                                    onChange={(e) =>
                                        props.setCreateProjectForm({
                                            ...props.createProjectForm,
                                            eigendom_soort: e.target.value,
                                        })
                                    }
                                />
                            </Stack>
                            <Stack>
                                <Typography variant="subtitle1" fontWeight="500">
                                    {t("createProject.houseBlocksForm.rentalPropertyPrivate")}
                                </Typography>
                                <TextField
                                    id="rentalPropertyPrivate"
                                    size="small"
                                    variant="outlined"
                                    value={props.createProjectForm ? props.createProjectForm["huurwoning particuliere verhuurder"] : ""}
                                    onChange={(e) =>
                                        props.setCreateProjectForm({
                                            ...props.createProjectForm,
                                            "huurwoning particuliere verhuurder": e.target.value,
                                        })
                                    }
                                />
                            </Stack>
                            <Stack>
                                <Typography variant="subtitle1" fontWeight="500">
                                    {t("createProject.houseBlocksForm.rentalPropertyHousingAssociation")}
                                </Typography>
                                <TextField
                                    id="rentalPropertyHousingAssociation"
                                    size="small"
                                    variant="outlined"
                                    value={props.createProjectForm ? props.createProjectForm["huurwoning woningcorporatie"] : ""}
                                    onChange={(e) =>
                                        props.setCreateProjectForm({
                                            ...props.createProjectForm,
                                            "huurwoning woningcorporatie": e.target.value,
                                        })
                                    }
                                />
                            </Stack>
                        </Stack>
                        <Stack width="49%">
                            <Stack>
                                <Typography variant="subtitle1" fontWeight="500">
                                    {t("createProject.houseBlocksForm.value")}
                                </Typography>
                                <TextField
                                    id="value"
                                    size="small"
                                    variant="outlined"
                                    InputProps={{
                                        endAdornment: <InputAdornment position="end">€</InputAdornment>,
                                    }}
                                    value={props.createProjectForm ? props.createProjectForm.waarde : ""}
                                    onChange={(e) =>
                                        props.setCreateProjectForm({
                                            ...props.createProjectForm,
                                            waarde: e.target.value,
                                        })
                                    }
                                />
                            </Stack>
                            <Stack>
                                <Typography variant="subtitle1" fontWeight="500">
                                    {t("createProject.houseBlocksForm.rentalAmount")}
                                </Typography>
                                <TextField
                                    id="rentalAmount"
                                    size="small"
                                    variant="outlined"
                                    InputProps={{
                                        endAdornment: <InputAdornment position="end">€</InputAdornment>,
                                    }}
                                    value={props.createProjectForm ? props.createProjectForm.huurbedrag : ""}
                                    onChange={(e) =>
                                        props.setCreateProjectForm({
                                            ...props.createProjectForm,
                                            huurbedrag: e.target.value,
                                        })
                                    }
                                />
                            </Stack>
                        </Stack>
                    </Stack>
                </Stack>
                <Stack direction="column" border="solid 1px #ddd" sx={{ backgroundColor: "#F9F9F9" }} p={2} mt={1} width="49%">
                    <Stack>
                        <Typography variant="subtitle1" fontWeight="600">
                            {t("createProject.houseBlocksForm.groundPosition")}
                        </Typography>
                    </Stack>
                    <Stack direction="column" justifyContent="space-between">
                        <Stack>
                            <Typography variant="subtitle1" fontWeight="500">
                                {t("createProject.houseBlocksForm.noPermissionFromLandOwner")}
                            </Typography>
                            <TextField
                                id="noPermissionFromLandOwner"
                                size="small"
                                variant="outlined"
                                value={props.createProjectForm ? props.createProjectForm["geen toestemming grondeigenaar"] : ""}
                                onChange={(e) =>
                                    props.setCreateProjectForm({
                                        ...props.createProjectForm,
                                        "geen toestemming grondeigenaar": e.target.value,
                                    })
                                }
                            />
                        </Stack>
                        <Stack>
                            <Typography variant="subtitle1" fontWeight="500">
                                {t("createProject.houseBlocksForm.intentionToCooperateWithTheLandowner")}
                            </Typography>
                            <TextField
                                id="intentionToCooperateWithTheLandowner"
                                size="small"
                                variant="outlined"
                                value={props.createProjectForm ? props.createProjectForm["intentie medewerking grondeigenaar"] : ""}
                                onChange={(e) =>
                                    props.setCreateProjectForm({
                                        ...props.createProjectForm,
                                        "intentie medewerking grondeigenaar": e.target.value,
                                    })
                                }
                            />
                        </Stack>
                        <Stack>
                            <Typography variant="subtitle1" fontWeight="500">
                                {t("createProject.houseBlocksForm.formalPermissionFromLandOwner")}
                            </Typography>
                            <TextField
                                id="formalPermissionFromLandOwner"
                                size="small"
                                variant="outlined"
                                value={props.createProjectForm ? props.createProjectForm["formele toestemming van grondeigenaar"] : ""}
                                onChange={(e) =>
                                    props.setCreateProjectForm({
                                        ...props.createProjectForm,
                                        "formele toestemming van grondeigenaar": e.target.value,
                                    })
                                }
                            />
                        </Stack>
                    </Stack>
                </Stack>
            </Stack>
            <Stack direction="column" alignItems="flex-start" justifyContent="space-between">
                <PhysicalAppeareanceGroup />
                <PurposeGroup />
            </Stack> */}
        </Box>
    );
};
