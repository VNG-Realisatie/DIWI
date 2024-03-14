import { Stack, Typography, TextField } from "@mui/material";
import { t } from "i18next";
import { MutationInformationProps } from "./MutationInformationGroup";
import { InputContainer } from "../InputContainer";

export const DemolitionPlanCapacityInput = ({ projectForm, setProjectForm, edit, editForm }: MutationInformationProps) => {
    return (
        <Stack>
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.houseBlocksForm.demolition")}
            </Typography>
            {edit && editForm && (
                <TextField
                    InputProps={{
                        inputProps: {
                            min: 0,
                        },
                    }}
                    type="number"
                    id="demolitionPlan"
                    size="small"
                    variant="outlined"
                    value={projectForm ? projectForm.mutation.demolition : null}
                    onChange={(e) =>
                        projectForm &&
                        setProjectForm({
                            ...projectForm,
                            mutation: { ...projectForm.mutation, demolition: +e.target.value },
                        })
                    }
                />
            )}
            {!edit && editForm && (
                <InputContainer>
                    <Typography>{projectForm?.mutation?.demolition}</Typography>
                </InputContainer>
            )}
        </Stack>
    );
};
