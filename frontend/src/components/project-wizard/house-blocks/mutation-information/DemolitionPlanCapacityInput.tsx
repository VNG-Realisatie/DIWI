import { Stack, Typography, TextField } from "@mui/material";
import { t } from "i18next";
import { MutationInformationProps } from "./MutationInformationGroup";

export const DemolitionPlanCapacityInput = ({ projectForm, setProjectForm }: MutationInformationProps) => {
    return (
        <Stack>
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.houseBlocksForm.demolition")}
            </Typography>
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
        </Stack>
    );
};
