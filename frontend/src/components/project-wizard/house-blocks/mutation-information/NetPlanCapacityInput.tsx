import { Stack, Typography, TextField } from "@mui/material";
import { t } from "i18next";
import { MutationInformationProps } from "./MutationInformationGroup";
import { InputContainer } from "../InputContainer";

export const NetPlanCapacityInput = ({ projectForm, setProjectForm, edit, editForm }: MutationInformationProps) => {
    return (
        <Stack>
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.houseBlocksForm.netPlanCapacity")}
            </Typography>
            {edit && editForm && (
                <TextField
                    InputProps={{
                        inputProps: {
                            min: 0,
                        },
                    }}
                    type="number"
                    id="netPlan"
                    size="small"
                    variant="outlined"
                    value={projectForm ? projectForm.mutation.netPlanCapacity : null}
                    onChange={(e) =>
                        projectForm &&
                        setProjectForm({
                            ...projectForm,
                            mutation: { ...projectForm.mutation, netPlanCapacity: +e.target.value },
                        })
                    }
                />
            )}
            {!edit && editForm && (
                <InputContainer>
                    <Typography>{projectForm?.mutation?.netPlanCapacity}</Typography>
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
                    id="netPlan"
                    size="small"
                    variant="outlined"
                    value={projectForm ? projectForm.mutation.netPlanCapacity : null}
                    onChange={(e) =>
                        projectForm &&
                        setProjectForm({
                            ...projectForm,
                            mutation: { ...projectForm.mutation, netPlanCapacity: +e.target.value },
                        })
                    }
                />
            )}
        </Stack>
    );
};
