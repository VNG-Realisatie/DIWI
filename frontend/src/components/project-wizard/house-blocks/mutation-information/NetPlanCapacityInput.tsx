import { Stack, Typography, TextField } from "@mui/material";
import { t } from "i18next";
import { MutationInformationProps } from "./MutationInformationGroup";

export const NetPlanCapacityInput = ({ projectForm, setProjectForm }: MutationInformationProps) => {
    return (
        <Stack>
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.houseBlocksForm.netPlanCapacity")}
            </Typography>
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
                value={projectForm ? projectForm.netPlanCapacity : null}
                onChange={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        netPlanCapacity: +e.target.value,
                    })
                }
            />
        </Stack>
    );
};
