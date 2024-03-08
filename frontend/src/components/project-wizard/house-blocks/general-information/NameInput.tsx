import { Stack, Typography, TextField } from "@mui/material";
import { t } from "i18next";
import { GeneralInformationProps } from "./GeneralInformationGroup";

export const NameInput = ({ projectForm, setProjectForm }: GeneralInformationProps) => {
    return (
        <Stack width="100%">
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.houseBlocksForm.nameLabel")}
            </Typography>
            <TextField
                id="name"
                size="small"
                variant="outlined"
                value={projectForm ? projectForm.houseblockName : ""}
                onChange={(e) =>
                    setProjectForm({
                        ...projectForm,
                        houseblockName: e.target.value,
                    })
                }
                fullWidth
            />
        </Stack>
    );
};
