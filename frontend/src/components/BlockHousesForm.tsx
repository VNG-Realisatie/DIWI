import { Box, InputAdornment, InputLabel, MenuItem, Select, Stack, TextField, Typography } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import mutatiesoort from "../api/json/enums/mutatie_soort.json";
import { useTranslation } from "react-i18next";

export const BlockHousesForm = (props: any) => {
    const { t } = useTranslation();
    return (
        <Box mt={4}>
            <Typography variant="h6" fontWeight="600">
                {t("createProject.houseBlocksForm.title")}
            </Typography>
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.houseBlocksForm.nameLabel")}
            </Typography>
            <Stack direction="row" alignItems="center" justifyContent="space-between">
                <TextField
                    id="name"
                    size="small"
                    variant="outlined"
                    value={props.createProjectForm ? props.createProjectForm.naam : ""}
                    onChange={(e) =>
                        props.setCreateProjectForm({
                            ...props.createProjectForm,
                            naam: e.target.value,
                        })
                    }
                    fullWidth
                />
            </Stack>

            <Stack
                direction="row"
                alignItems="flex-end"
                justifyContent="flex-start"
                spacing={2}
                border="solid 1px #ddd"
                mt={1}
                p={2}
                sx={{ backgroundColor: "#F9F9F9" }}
            >
                <Stack>
                    <Typography variant="subtitle1" fontWeight="500">
                        {t("createProject.houseBlocksForm.startDate")}
                    </Typography>
                    <DatePicker
                        value={props.createProjectForm ? props.createProjectForm["start datum"] : ""}
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
                        {t("createProject.houseBlocksForm.endDate")}
                    </Typography>
                    <DatePicker
                        value={props.createProjectForm ? props.createProjectForm["eind datum"] : ""}
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                "eind datum": e,
                            })
                        }
                    />
                </Stack>
            </Stack>
            <Stack border="solid 1px #ddd" mt={1} p={2} sx={{ backgroundColor: "#F9F9F9" }} direction="column">
                <Stack>
                    <Typography variant="subtitle1" fontWeight="600">
                        {t("createProject.houseBlocksForm.mutationData")}
                    </Typography>
                </Stack>
                <Stack direction="row" alignItems="flex-end" justifyContent="flex-start" spacing={2}>
                    <Stack>
                        <Typography variant="subtitle1" fontWeight="500">
                            {t("createProject.houseBlocksForm.grossPlanCapacity")}
                        </Typography>
                        <TextField
                            id="grossPlan"
                            size="small"
                            variant="outlined"
                            value={props.createProjectForm ? props.createProjectForm["bruto_plancapaciteit"] : ""}
                            onChange={(e) =>
                                props.setCreateProjectForm({
                                    ...props.createProjectForm,
                                    bruto_plancapaciteit: e.target.value,
                                })
                            }
                        />
                    </Stack>
                    <Stack>
                        <Typography variant="subtitle1" fontWeight="500">
                            {t("createProject.houseBlocksForm.netPlanCapacity")}
                        </Typography>
                        <TextField
                            id="netplan"
                            size="small"
                            variant="outlined"
                            value={props.createProjectForm ? props.createProjectForm["netto_plancapaciteit"] : ""}
                            onChange={(e) =>
                                props.setCreateProjectForm({
                                    ...props.createProjectForm,
                                    netto_plancapaciteit: e.target.value,
                                })
                            }
                        />
                    </Stack>
                    <Stack>
                        <Typography variant="subtitle1" fontWeight="500">
                            {t("createProject.houseBlocksForm.demolition")}
                        </Typography>
                        <TextField
                            id="demolition"
                            size="small"
                            variant="outlined"
                            value={props.createProjectForm ? props.createProjectForm.sloop : ""}
                            onChange={(e) =>
                                props.setCreateProjectForm({
                                    ...props.createProjectForm,
                                    sloop: e.target.value,
                                })
                            }
                        />
                    </Stack>
                    <Stack>
                        <InputLabel id="mutationtype"> {t("createProject.houseBlocksForm.mutationType")}</InputLabel>
                        <Select
                            size="small"
                            labelId="mutationtype"
                            id="fase"
                            value={props.createProjectForm ? props.createProjectForm.mutatie_soort : ""}
                            label={t("createProject.houseBlocksForm.mutationType")}
                            onChange={(e) =>
                                props.setCreateProjectForm({
                                    ...props.createProjectForm,
                                    mutatie_soort: e.target.value,
                                })
                            }
                        >
                            {mutatiesoort.map((m) => {
                                return (
                                    <MenuItem key={m} value={m}>
                                        {m}
                                    </MenuItem>
                                );
                            })}
                        </Select>
                    </Stack>
                </Stack>
            </Stack>
            <Stack>
                <Typography variant="subtitle1" fontWeight="500">
                    {t("createProject.houseBlocksForm.size")}
                </Typography>
                <TextField
                    id="size"
                    size="small"
                    variant="outlined"
                    value={props.createProjectForm ? props.createProjectForm.grootte : ""}
                    onChange={(e) =>
                        props.setCreateProjectForm({
                            ...props.createProjectForm,
                            grootte: e.target.value,
                        })
                    }
                />
            </Stack>
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
                {/* Fysiek Group Start */}
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
                {/* Fysiek Group End */}
                {/* Doel Group Start */}
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
                {/* Doel Group End */}
            </Stack>
        </Box>
    );
};
