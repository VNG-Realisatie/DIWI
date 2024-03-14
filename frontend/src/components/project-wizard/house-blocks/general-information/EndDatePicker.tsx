import { Stack, Typography } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import { t } from "i18next";
import { GeneralInformationProps } from "./GeneralInformationGroup";
import dayjs from "dayjs";
import { InputContainer } from "../InputContainer";

export const EndDatePicker = ({ projectForm, setProjectForm, edit, editForm }: GeneralInformationProps) => {
    return (
        <Stack width="100%">
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.houseBlocksForm.endDate")}
            </Typography>
            {edit && editForm && (
                <DatePicker
                    value={projectForm ? dayjs(projectForm.endDate) : null}
                    onChange={(e) =>
                        setProjectForm({
                            ...projectForm,
                            endDate: e instanceof Date ? e.toISOString() : "",
                        })
                    }
                />
            )}
            {!edit && editForm && (
                <InputContainer>
                    <Typography>{projectForm?.endDate}</Typography>
                </InputContainer>
            )}
            {!edit && !editForm && (
                <DatePicker
                    value={projectForm ? dayjs(projectForm.endDate) : null}
                    onChange={(e) =>
                        setProjectForm({
                            ...projectForm,
                            endDate: e instanceof Date ? e.toISOString() : "",
                        })
                    }
                />
            )}
        </Stack>
    );
};
