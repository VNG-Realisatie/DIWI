import { Stack, Typography, TextField } from "@mui/material";
import { t } from "i18next";
import { PurposeInformationProps } from "./PurposeGroup";
type Props = {
    state: PurposeInformationProps;
    value: string;
    translationPath: string;
};
export const SingleNumberInput = ({ state, value, translationPath }: Props) => {
    return (
        <Stack direction="row" alignItems="center" spacing={2} my={2}>
            <Typography variant="subtitle1" fontWeight="500" border="solid 1px #ddd" borderRadius="5px" p={0.6} flex={3}>
                {t(`${translationPath}.${value}`)}
            </Typography>
            <TextField
                sx={{ flex: 1 }}
                InputProps={{
                    inputProps: {
                        min: 0,
                    },
                }}
                type="number"
                id={value ? value : ""}
                size="small"
                variant="outlined"
                value={state.projectForm ? state.projectForm.purpose[value] : null}
                onChange={(e) =>
                    state.projectForm &&
                    state.setProjectForm({
                        ...state.projectForm,
                        purpose: {
                            ...state.projectForm.purpose,
                            [value]: +e.target.value,
                        },
                    })
                }
            />
        </Stack>
    );
};
