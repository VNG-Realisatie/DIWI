import { Stack, Typography } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import { t } from "i18next";
import { GeneralInformationProps } from "./GeneralInformationGroup";
import dayjs from "dayjs";
import { InputContainer } from "../InputContainer";

export const StartDatePicker = ({ projectForm, setProjectForm, edit, editForm }: GeneralInformationProps) => {
    return (
        <Stack width="100%">
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.houseBlocksForm.startDate")}
            </Typography>
            {edit && editForm && (
                <DatePicker
                    value={projectForm ? dayjs(projectForm.startDate) : null}
                    onChange={(e) =>
                        setProjectForm({
                            ...projectForm,
                            startDate: e instanceof Date ? e.toISOString() : null,
                        })
                    }
                />
            )}
            {!edit && editForm && (
                <InputContainer>
                    <Typography>{projectForm.startDate}</Typography>
                </InputContainer>
            )}
            {!edit && !editForm && (
                <DatePicker
                    value={projectForm ? dayjs(projectForm.startDate) : null}
                    onChange={(e) =>
                        setProjectForm({
                            ...projectForm,
                            startDate: e instanceof Date ? e.toISOString() : null,
                        })
                    }
                />
            )}
        </Stack>
    );
};
