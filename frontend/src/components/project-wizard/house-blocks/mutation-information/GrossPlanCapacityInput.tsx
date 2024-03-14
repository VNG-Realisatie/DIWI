import { Stack, Typography, TextField } from "@mui/material";
import { t } from "i18next";
import { MutationInformationProps } from "./MutationInformationGroup";
import { InputContainer } from "../InputContainer";

export const GrossPlanCapacityInput = ({ projectForm, setProjectForm, edit, editForm }: MutationInformationProps) => {
    return (
        <Stack>
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.houseBlocksForm.grossPlanCapacity")}
            </Typography>
            {edit && editForm && (
                <TextField
                    InputProps={{
                        inputProps: {
                            min: 0,
                        },
                    }}
                    type="number"
                    id="grossPlan"
                    size="small"
                    variant="outlined"
                    value={projectForm ? projectForm.mutation.grossPlanCapacity : null}
                    onChange={(e) =>
                        projectForm &&
                        setProjectForm({
                            ...projectForm,
                            mutation: {
                                ...projectForm.mutation,
                                grossPlanCapacity: +e.target.value,
                            },
                        })
                    }
                />
            )}
            {!edit && editForm && (
                <InputContainer>
                    <Typography>{projectForm?.mutation?.grossPlanCapacity}</Typography>
                </InputContainer>
            )}
            {!edit && !editForm && (
                <TextField
                    InputProps={{
                        inputProps: {
                            min: 0,
                        },
                    }}
                    type="number"
                    id="grossPlan"
                    size="small"
                    variant="outlined"
                    value={projectForm ? projectForm.mutation.grossPlanCapacity : null}
                    onChange={(e) =>
                        projectForm &&
                        setProjectForm({
                            ...projectForm,
                            mutation: {
                                ...projectForm.mutation,
                                grossPlanCapacity: +e.target.value,
                            },
                        })
                    }
                />
            )}
        </Stack>
    );
};
