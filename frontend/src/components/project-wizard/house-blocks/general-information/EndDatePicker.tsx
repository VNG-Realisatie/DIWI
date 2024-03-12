import { Stack, Typography } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import { t } from "i18next";
import { GeneralInformationProps } from "./GeneralInformationGroup";
import dayjs from "dayjs";

export const EndDatePicker = ({ projectForm, setProjectForm }: GeneralInformationProps) => {
    return (
        <Stack width="100%">
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.houseBlocksForm.endDate")}
            </Typography>
            <DatePicker
                value={projectForm ? dayjs(projectForm.endDate) : null}
                onChange={(e) =>
                    setProjectForm({
                        ...projectForm,
                        endDate: e instanceof Date ? e.toISOString() : "",
                    })
                }
            />
        </Stack>
    );
};
