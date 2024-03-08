import { Stack, Typography, TextField } from "@mui/material";
import { t } from "i18next";
import { MutationInformationProps } from "./MutationInformationGroup";

export const GrossPlanCapacityInput = ({ projectForm, setProjectForm }: MutationInformationProps) => {
    return (
        <Stack>
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.houseBlocksForm.grossPlanCapacity")}
            </Typography>
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
                value={projectForm ? projectForm.grossPlanCapacity : null}
                onChange={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        grossPlanCapacity: +e.target.value,
                    })
                }
            />
        </Stack>
    );
};
