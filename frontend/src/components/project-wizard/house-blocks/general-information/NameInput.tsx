import { Stack, Typography, TextField } from "@mui/material";
import { t } from "i18next";
import { GeneralInformationProps } from "./GeneralInformationGroup";
import { InputContainer } from "../InputContainer";

export const NameInput = ({ projectForm, setProjectForm, edit, editForm }: GeneralInformationProps) => {
    return (
        <Stack width="100%">
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.houseBlocksForm.nameLabel")}
            </Typography>
            {edit && editForm && (
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
            )}
            {!edit && editForm && (
                <InputContainer>
                    <Typography>{projectForm ? projectForm.houseblockName : ""}</Typography>
                </InputContainer>
            )}
            {!edit && !editForm && (
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
            )}
        </Stack>
    );
};
