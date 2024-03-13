import { Stack, TextField, Typography } from "@mui/material";
import { t } from "i18next";
import { GeneralInformationProps } from "./GeneralInformationGroup";

export const SizeInput = ({ projectForm, setProjectForm }: GeneralInformationProps) => {
    const handleSizeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newSize = {
            value: +e.target.value,
            min: null,
            max: null,
        };

        setProjectForm({
            ...projectForm,
            size: newSize,
        });
    };
    return (
        <Stack width="100%">
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.houseBlocksForm.size")}
            </Typography>
            <TextField
                InputProps={{
                    inputProps: {
                        min: 0,
                    },
                }}
                type="number"
                id="size"
                size="small"
                variant="outlined"
                value={projectForm ? projectForm?.size?.value : 0}
                onChange={handleSizeChange}
            />
        </Stack>
    );
};
