import {
    Box,
    InputAdornment,
    InputLabel,
    MenuItem,
    Select,
    Stack,
    TextField,
    Typography,
} from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import { mutatiesoort } from "../widgets/constants";

export const BlockHousesForm = (props: any) => {
    return (
        <Box mt={4}>
            <Typography variant="h6" fontWeight="600">
                Vul de huizenblokken in
            </Typography>
            <Typography variant="subtitle1" fontWeight="500">
                Naam
            </Typography>
            <Stack
                direction="row"
                alignItems="center"
                justifyContent="space-between"
            >
                <TextField
                    id="naam"
                    size="small"
                    variant="outlined"
                    value={
                        props.createProjectForm
                            ? props.createProjectForm.naam
                            : ""
                    }
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
                justifyContent="space-between"
                border="solid 1px #ddd"
                mt={1}
                p={2}
                sx={{ backgroundColor: "#F9F9F9" }}
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
                <Stack>
                    <Typography variant="subtitle1" fontWeight="500">
                        Huurwoning Particuliere verhuurder
                    </Typography>
                    <TextField
                        id="huurwoningParticuliereverhuurder"
                        size="small"
                        variant="outlined"
                        value={
                            props.createProjectForm
                                ? props.createProjectForm[
                                      "huurwoning particuliere verhuurder"
                                  ]
                                : ""
                        }
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                "huurwoning particuliere verhuurder":
                                    e.target.value,
                            })
                        }
                    />
                </Stack>
                <Stack>
                    <Typography variant="subtitle1" fontWeight="500">
                        Huurwoning Woningcorporatie
                    </Typography>
                    <TextField
                        id="huurwoningWoningcorporatie"
                        size="small"
                        variant="outlined"
                        value={
                            props.createProjectForm
                                ? props.createProjectForm[
                                      "huurwoning woningcorporatie"
                                  ]
                                : ""
                        }
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                "huurwoning woningcorporatie": e.target.value,
                            })
                        }
                    />
                </Stack>
            </Stack>

            <Stack
                direction="row"
                alignItems="flex-end"
                justifyContent="space-between"
                border="solid 1px #ddd"
                mt={1}
                p={2}
                sx={{ backgroundColor: "#F9F9F9" }}
            >
                <Stack>
                    <Typography variant="subtitle1" fontWeight="500">
                        Bruto plancapaciteit
                    </Typography>
                    <TextField
                        id="brutoplan"
                        size="small"
                        variant="outlined"
                        value={
                            props.createProjectForm
                                ? props.createProjectForm[
                                      "bruto plancapaciteit"
                                  ]
                                : ""
                        }
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                "bruto plancapaciteit": e.target.value,
                            })
                        }
                    />
                </Stack>
                <Stack>
                    <Typography variant="subtitle1" fontWeight="500">
                        Netto Plancapaciteit
                    </Typography>
                    <TextField
                        id="nettoplan"
                        size="small"
                        variant="outlined"
                        value={
                            props.createProjectForm
                                ? props.createProjectForm[
                                      "netto plancapaciteit"
                                  ]
                                : ""
                        }
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                "netto plancapaciteit": e.target.value,
                            })
                        }
                    />
                </Stack>
                <Stack>
                    <Typography variant="subtitle1" fontWeight="500">
                        Sloop
                    </Typography>
                    <TextField
                        id="sloop"
                        size="small"
                        variant="outlined"
                        value={
                            props.createProjectForm
                                ? props.createProjectForm.sloop
                                : ""
                        }
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                sloop: e.target.value,
                            })
                        }
                    />
                </Stack>
                <Stack>
                    <Typography variant="subtitle1" fontWeight="500">
                        Grootte
                    </Typography>
                    <TextField
                        id="grootte"
                        size="small"
                        variant="outlined"
                        value={
                            props.createProjectForm
                                ? props.createProjectForm.grootte
                                : ""
                        }
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                grootte: e.target.value,
                            })
                        }
                    />
                </Stack>
                <Stack>
                    <Typography variant="subtitle1" fontWeight="500">
                        Koopwoning
                    </Typography>
                    <TextField
                        id="koopwoning"
                        size="small"
                        variant="outlined"
                        value={
                            props.createProjectForm
                                ? props.createProjectForm.koopwoning
                                : ""
                        }
                        onChange={(e) =>
                            props.setCreateProjectForm({
                                ...props.createProjectForm,
                                koopwoning: e.target.value,
                            })
                        }
                    />
                </Stack>
            </Stack>

            <Stack
                direction="row"
                alignItems="flex-end"
                justifyContent="space-between"
            >
                <Stack
                    direction="row"
                    alignItems="flex-start"
                    justifyContent="space-between"
                    border="solid 1px #ddd"
                    sx={{ backgroundColor: "#F9F9F9" }}
                    p={2}
                    pt={4}
                    width="49%"
                >
                    <Stack width="49%">
                        <Stack>
                            <Typography variant="subtitle1" fontWeight="500">
                                Eengezinswoning
                            </Typography>
                            <TextField
                                id="eengezinswoning"
                                size="small"
                                variant="outlined"
                                value={
                                    props.createProjectForm
                                        ? props.createProjectForm
                                              .eengezinswoning
                                        : ""
                                }
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
                                Meergezinswoning
                            </Typography>
                            <TextField
                                id="meergezinswoning"
                                size="small"
                                variant="outlined"
                                value={
                                    props.createProjectForm
                                        ? props.createProjectForm
                                              .meergezinswoning
                                        : ""
                                }
                                onChange={(e) =>
                                    props.setCreateProjectForm({
                                        ...props.createProjectForm,
                                        meergezinswoning: e.target.value,
                                    })
                                }
                            />
                        </Stack>
                        <Stack>
                            <InputLabel id="mutatiesoort">
                                Mutatiesoort
                            </InputLabel>
                            <Select
                                sx={{ width: "100%" }}
                                labelId="mutatiesoort"
                                id="fase"
                                value={
                                    props.createProjectForm
                                        ? props.createProjectForm.mutatiesoort
                                        : ""
                                }
                                label="Project Fase"
                                onChange={(e) =>
                                    props.setCreateProjectForm({
                                        ...props.createProjectForm,
                                        mutatiesoort: e.target.value,
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
                    <Stack width="49%">
                        <Stack>
                            <Typography variant="subtitle1" fontWeight="500">
                                Waarde
                            </Typography>
                            <TextField
                                id="waarde"
                                size="small"
                                variant="outlined"
                                InputProps={{
                                    endAdornment: (
                                        <InputAdornment position="end">
                                            €
                                        </InputAdornment>
                                    ),
                                }}
                                value={
                                    props.createProjectForm
                                        ? props.createProjectForm.waarde
                                        : ""
                                }
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
                                Huurbedrag
                            </Typography>
                            <TextField
                                id="huurbedrag"
                                size="small"
                                variant="outlined"
                                InputProps={{
                                    endAdornment: (
                                        <InputAdornment position="end">
                                            €
                                        </InputAdornment>
                                    ),
                                }}
                                value={
                                    props.createProjectForm
                                        ? props.createProjectForm.huurbedrag
                                        : ""
                                }
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
                <Stack
                    direction="column"
                    border="solid 1px #ddd"
                    sx={{ backgroundColor: "#F9F9F9" }}
                    p={2}
                    mt={1}
                    width="49%"
                >
                    <Stack>
                        <Typography variant="subtitle1" fontWeight="600">
                            Grondpositie
                        </Typography>
                    </Stack>
                    <Stack direction="column" justifyContent="space-between">
                        <Stack>
                            <Typography variant="subtitle1" fontWeight="500">
                                Geen toestemming Grondeigenaar
                            </Typography>
                            <TextField
                                id="geenToestemmingGrondeigenaar"
                                size="small"
                                variant="outlined"
                                value={
                                    props.createProjectForm
                                        ? props.createProjectForm[
                                              "geen toestemming grondeigenaar"
                                          ]
                                        : ""
                                }
                                onChange={(e) =>
                                    props.setCreateProjectForm({
                                        ...props.createProjectForm,
                                        "geen toestemming grondeigenaar":
                                            e.target.value,
                                    })
                                }
                            />
                        </Stack>
                        <Stack>
                            <Typography variant="subtitle1" fontWeight="500">
                                Intentie medewerking Grondeigenaar
                            </Typography>
                            <TextField
                                id="intentieMedewerkingGrondeigenaar"
                                size="small"
                                variant="outlined"
                                value={
                                    props.createProjectForm
                                        ? props.createProjectForm[
                                              "intentie medewerking grondeigenaar"
                                          ]
                                        : ""
                                }
                                onChange={(e) =>
                                    props.setCreateProjectForm({
                                        ...props.createProjectForm,
                                        "intentie medewerking grondeigenaar":
                                            e.target.value,
                                    })
                                }
                            />
                        </Stack>
                        <Stack>
                            <Typography variant="subtitle1" fontWeight="500">
                                Formele toestemming van Grondeigenaar
                            </Typography>
                            <TextField
                                id="FormeleToestemmingVanGrondeigenaar"
                                size="small"
                                variant="outlined"
                                value={
                                    props.createProjectForm
                                        ? props.createProjectForm[
                                              "formele toestemming van grondeigenaar"
                                          ]
                                        : ""
                                }
                                onChange={(e) =>
                                    props.setCreateProjectForm({
                                        ...props.createProjectForm,
                                        "formele toestemming van grondeigenaar":
                                            e.target.value,
                                    })
                                }
                            />
                        </Stack>
                    </Stack>
                </Stack>
            </Stack>
            <Stack
                direction="row"
                alignItems="center"
                justifyContent="space-between"
            >
                {/* Fysiek Group Start */}
                <Stack
                    mt={1}
                    direction="column"
                    border="solid 1px #ddd"
                    sx={{ backgroundColor: "#F9F9F9" }}
                    width="49%"
                    p={2}
                >
                    <Typography variant="subtitle1" fontWeight="600">
                        Fysiek voorkomen
                    </Typography>
                    <Stack direction="row" justifyContent="space-around">
                        <Stack
                            alignItems="flex-start"
                            justifyContent="flex-start"
                        >
                            <Stack>
                                <Typography
                                    variant="subtitle1"
                                    fontWeight="500"
                                >
                                    Tussenwoning
                                </Typography>
                                <TextField
                                    id="tussenwoning"
                                    size="small"
                                    variant="outlined"
                                    value={
                                        props.createProjectForm
                                            ? props.createProjectForm
                                                  .tussenwoning
                                            : ""
                                    }
                                    onChange={(e) =>
                                        props.setCreateProjectForm({
                                            ...props.createProjectForm,
                                            tussenwoning: e.target.value,
                                        })
                                    }
                                />
                            </Stack>
                            <Stack>
                                <Typography
                                    variant="subtitle1"
                                    fontWeight="500"
                                >
                                    Hoekwoning
                                </Typography>
                                <TextField
                                    id="hoekwoning"
                                    size="small"
                                    variant="outlined"
                                    value={
                                        props.createProjectForm
                                            ? props.createProjectForm.hoekwoning
                                            : ""
                                    }
                                    onChange={(e) =>
                                        props.setCreateProjectForm({
                                            ...props.createProjectForm,
                                            hoekwoning: e.target.value,
                                        })
                                    }
                                />
                            </Stack>
                            <Stack>
                                <Typography
                                    variant="subtitle1"
                                    fontWeight="500"
                                >
                                    2 onder een kap
                                </Typography>
                                <TextField
                                    id="tweeondereenkap"
                                    size="small"
                                    variant="outlined"
                                    value={
                                        props.createProjectForm
                                            ? props.createProjectForm
                                                  .tweeondereenkap
                                            : ""
                                    }
                                    onChange={(e) =>
                                        props.setCreateProjectForm({
                                            ...props.createProjectForm,
                                            tweeondereenkap: e.target.value,
                                        })
                                    }
                                />
                            </Stack>
                        </Stack>
                        <Stack
                            alignItems="flex-start"
                            justifyContent="flex-start"
                            ml={2}
                        >
                            <Stack>
                                <Typography
                                    variant="subtitle1"
                                    fontWeight="500"
                                >
                                    Vrijstaand
                                </Typography>
                                <TextField
                                    id="vrijstaand"
                                    size="small"
                                    variant="outlined"
                                    value={
                                        props.createProjectForm
                                            ? props.createProjectForm.vrijstaand
                                            : ""
                                    }
                                    onChange={(e) =>
                                        props.setCreateProjectForm({
                                            ...props.createProjectForm,
                                            vrijstaand: e.target.value,
                                        })
                                    }
                                />
                            </Stack>
                            <Stack>
                                <Typography
                                    variant="subtitle1"
                                    fontWeight="500"
                                >
                                    Portiekflat
                                </Typography>
                                <TextField
                                    id="portiekflat"
                                    size="small"
                                    variant="outlined"
                                    value={
                                        props.createProjectForm
                                            ? props.createProjectForm
                                                  .portiekflat
                                            : ""
                                    }
                                    onChange={(e) =>
                                        props.setCreateProjectForm({
                                            ...props.createProjectForm,
                                            portiekflat: e.target.value,
                                        })
                                    }
                                />
                            </Stack>

                            <Stack>
                                <Typography
                                    variant="subtitle1"
                                    fontWeight="500"
                                >
                                    Gallerijflat
                                </Typography>
                                <TextField
                                    id="gallerijflat"
                                    size="small"
                                    variant="outlined"
                                    value={
                                        props.createProjectForm
                                            ? props.createProjectForm
                                                  .gallerijflat
                                            : ""
                                    }
                                    onChange={(e) =>
                                        props.setCreateProjectForm({
                                            ...props.createProjectForm,
                                            gallerijflat: e.target.value,
                                        })
                                    }
                                />
                            </Stack>
                        </Stack>
                    </Stack>
                </Stack>
                {/* Fysiek Group End */}
                {/* Doel Group Start */}
                <Stack
                    mt={1}
                    direction="column"
                    border="solid 1px #ddd"
                    sx={{ backgroundColor: "#F9F9F9" }}
                    width="49%"
                    p={2}
                >
                    <Typography variant="subtitle1" fontWeight="600">
                        Doel
                    </Typography>
                    <Stack direction="row" justifyContent="space-around">
                        <Stack
                            alignItems="flex-start"
                            justifyContent="flex-start"
                        >
                            <Stack>
                                <Typography
                                    variant="subtitle1"
                                    fontWeight="500"
                                >
                                    Regulier
                                </Typography>
                                <TextField
                                    id="regulier"
                                    size="small"
                                    variant="outlined"
                                    value={
                                        props.createProjectForm
                                            ? props.createProjectForm.regulier
                                            : ""
                                    }
                                    onChange={(e) =>
                                        props.setCreateProjectForm({
                                            ...props.createProjectForm,
                                            regulier: e.target.value,
                                        })
                                    }
                                />
                            </Stack>
                            <Stack>
                                <Typography
                                    variant="subtitle1"
                                    fontWeight="500"
                                >
                                    Jongeren
                                </Typography>
                                <TextField
                                    id="jongeren"
                                    size="small"
                                    variant="outlined"
                                    value={
                                        props.createProjectForm
                                            ? props.createProjectForm.jongeren
                                            : ""
                                    }
                                    onChange={(e) =>
                                        props.setCreateProjectForm({
                                            ...props.createProjectForm,
                                            jongeren: e.target.value,
                                        })
                                    }
                                />
                            </Stack>
                            <Stack>
                                <Typography
                                    variant="subtitle1"
                                    fontWeight="500"
                                >
                                    Studenten
                                </Typography>
                                <TextField
                                    id=" sudenten"
                                    size="small"
                                    variant="outlined"
                                    value={
                                        props.createProjectForm
                                            ? props.createProjectForm.sudenten
                                            : ""
                                    }
                                    onChange={(e) =>
                                        props.setCreateProjectForm({
                                            ...props.createProjectForm,
                                            sudenten: e.target.value,
                                        })
                                    }
                                />
                            </Stack>
                        </Stack>
                        <Stack
                            alignItems="flex-start"
                            justifyContent="flex-start"
                            ml={2}
                        >
                            <Stack>
                                <Typography
                                    variant="subtitle1"
                                    fontWeight="500"
                                >
                                    Ouderen
                                </Typography>
                                <TextField
                                    id="ouderen"
                                    size="small"
                                    variant="outlined"
                                    value={
                                        props.createProjectForm
                                            ? props.createProjectForm.ouderen
                                            : ""
                                    }
                                    onChange={(e) =>
                                        props.setCreateProjectForm({
                                            ...props.createProjectForm,
                                            ouderen: e.target.value,
                                        })
                                    }
                                />
                            </Stack>
                            <Stack>
                                <Typography
                                    variant="subtitle1"
                                    fontWeight="500"
                                >
                                    Gehandicapter en zorg
                                </Typography>
                                <TextField
                                    id="gehandicapterenzorg"
                                    size="small"
                                    variant="outlined"
                                    value={
                                        props.createProjectForm
                                            ? props.createProjectForm
                                                  .gehandicapterenzorg
                                            : ""
                                    }
                                    onChange={(e) =>
                                        props.setCreateProjectForm({
                                            ...props.createProjectForm,
                                            gehandicapterenzorg: e.target.value,
                                        })
                                    }
                                />
                            </Stack>

                            <Stack>
                                <Typography
                                    variant="subtitle1"
                                    fontWeight="500"
                                >
                                    Grote gezinnen
                                </Typography>
                                <TextField
                                    id="grotegezinnen"
                                    size="small"
                                    variant="outlined"
                                    value={
                                        props.createProjectForm
                                            ? props.createProjectForm
                                                  .grotegezinnen
                                            : ""
                                    }
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
                </Stack>
                {/* Doel Group End */}
            </Stack>
        </Box>
    );
};
