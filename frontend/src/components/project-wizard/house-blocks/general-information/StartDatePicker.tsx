import { Stack, Typography } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import { t } from "i18next";
import { GeneralInformationProps } from "./GeneralInformationGroup";

export const StartDatePicker = ({ projectForm, setProjectForm }: GeneralInformationProps) => {
    return (
        <Stack width="100%">
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.houseBlocksForm.startDate")}
            </Typography>
            <DatePicker
                value={projectForm ? projectForm.startDate : ""}
                onChange={(e) =>
                    setProjectForm({
                        ...projectForm,
                        startDate: e,
                    })
                }
            />
        </Stack>
    );
};
