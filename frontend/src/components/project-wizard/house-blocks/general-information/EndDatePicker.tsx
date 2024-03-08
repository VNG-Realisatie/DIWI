import { Stack, Typography } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import { t } from "i18next";
import { GeneralInformationProps } from "./GeneralInformationGroup";

export const EndDatePicker = ({ projectForm, setProjectForm }: GeneralInformationProps) => {
    return (
        <Stack width="100%">
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.houseBlocksForm.endDate")}
            </Typography>
            <DatePicker
                value={projectForm ? projectForm.endDate : ""}
                onChange={(e) =>
                    setProjectForm({
                        ...projectForm,
                        endDate: e,
                    })
                }
            />
        </Stack>
    );
};
