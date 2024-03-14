import { Stack, Typography, TextField, Box } from "@mui/material";
import { t } from "i18next";
import { PhysicalAppeareanceInformationProps } from "./PhysicalAppeareanceGroup";
import { InputContainer } from "../InputContainer";
type Props = {
    state: PhysicalAppeareanceInformationProps;
    value: string;
    translationPath: string;
};
export const SingleNumberInput = ({ state, value, translationPath }: Props) => {
    return (
        <Stack direction="row" alignItems="center" spacing={2} my={2}>
            <Typography variant="subtitle1" fontWeight="500" border="solid 1px #ddd" borderRadius="5px" p={0.6} flex={3}>
                {t(`${translationPath}.${value}`)}
            </Typography>
            {state.edit && state.editForm && (
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
                    value={state.projectForm ? state.projectForm.physicalAppeareance[value] : null}
                    onChange={(e) =>
                        state.projectForm &&
                        state.setProjectForm({
                            ...state.projectForm,
                            physicalAppeareance: {
                                ...state.projectForm.physicalAppeareance,
                                [value]: +e.target.value,
                            },
                        })
                    }
                />
            )}
            {!state.edit && state.editForm && (
                <Box sx={{ flex: 1 }}>
                    <InputContainer>
                        <Typography>{state?.projectForm?.physicalAppeareance[value]}</Typography>
                    </InputContainer>
                </Box>
            )}
            {!state.edit && !state.editForm && (
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
                    value={state.projectForm ? state.projectForm.physicalAppeareance[value] : null}
                    onChange={(e) =>
                        state.projectForm &&
                        state.setProjectForm({
                            ...state.projectForm,
                            physicalAppeareance: {
                                ...state.projectForm.physicalAppeareance,
                                [value]: +e.target.value,
                            },
                        })
                    }
                />
            )}
        </Stack>
    );
};
