import { Autocomplete, Box, Stack, TextField, Typography } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import { HouseBlockItem, StatusType, houseBlocks, statuses } from "../api/dummyData";
import { useTranslation } from "react-i18next";

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const TimelineForm = (props: any) => {
    const { t } = useTranslation();
    return (
        <Box mt={4} position="relative">
            <Typography variant="h6" fontWeight="600">
                {t("createProject.timelineForm.title")}
            </Typography>

            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.timelineForm.deliveryDate")}
            </Typography>
            <DatePicker
                value={props.createProjectForm ? props.createProjectForm.opleverDatum : ""}
                onChange={(newValue) =>
                    props.setCreateProjectForm({
                        ...props.createProjectForm,
                        opleverDatum: newValue,
                    })
                }
            />
            <Stack direction="row" alignItems="flex-end" justifyContent="space-between" position="relative">
                <Autocomplete
                    sx={{ width: "250px" }}
                    options={houseBlocks}
                    getOptionLabel={(option: HouseBlockItem) => option.name}
                    value={props.createProjectForm ? props.createProjectForm.houseBlock : ""}
                    // eslint-disable-next-line @typescript-eslint/no-explicit-any
                    onChange={(event: any, newValue: HouseBlockItem | null) => {
                        props.setCreateProjectForm({
                            ...props.createProjectForm,
                            houseBlock: newValue,
                        });
                    }}
                    renderInput={(params) => <TextField {...params} label={t("createProject.timelineForm.houseBlock")} variant="standard" />}
                />
                <Typography variant="body1">{t("createProject.timelineForm.expectedDelivery")}</Typography>
                <Autocomplete
                    sx={{ width: "250px" }}
                    options={statuses}
                    getOptionLabel={(option: StatusType) => option.status}
                    value={props.createProjectForm ? props.createProjectForm.status : ""}
                    // eslint-disable-next-line @typescript-eslint/no-explicit-any
                    onChange={(event: any, newValue: StatusType | null) => {
                        props.setCreateProjectForm({
                            ...props.createProjectForm,
                            status: newValue,
                        });
                    }}
                    renderInput={(params) => <TextField {...params} label={t("createProject.timelineForm.status")} variant="standard" />}
                />
                <Typography variant="body1">{t("createProject.timelineForm.is")}</Typography>

                <DatePicker
                    value={props.createProjectForm ? props.createProjectForm.statusDate : ""}
                    onChange={(newValue) =>
                        props.setCreateProjectForm({
                            ...props.createProjectForm,
                            statusDate: newValue,
                        })
                    }
                />
            </Stack>
        </Box>
    );
};
