import { Stack, Typography, TextField, Box } from "@mui/material";
import { t } from "i18next";
import { InputContainer } from "../InputContainer";
type Props = {
    value: string;
    translationPath: string;
    property: number | null;
    update: (propert: number | null) => void;
    edit: boolean;
    editForm: boolean;
};
type SingleNumberEdit = {
    value: string;
    property: number | null;
    update: (propert: number | null) => void;
};
const SingleNumberEditInput = ({ value, property, update }: SingleNumberEdit) => {
    return (
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
            value={property ? property : null}
            onChange={(e) => update(+e.target.value)}
        />
    );
};
export const SingleNumberInput = ({ property, update, value, translationPath, edit, editForm }: Props) => {
    return (
        <Stack direction="row" alignItems="center" spacing={2} my={2}>
            <Typography variant="subtitle1" fontWeight="500" border="solid 1px #ddd" borderRadius="5px" p={0.6} flex={3}>
                {t(`${translationPath}.${value}`)}
            </Typography>
            {edit && editForm && <SingleNumberEditInput value={value} property={property} update={update} />}
            {!edit && editForm && (
                <Box sx={{ flex: 1 }}>
                    <InputContainer>
                        <Typography>{property}</Typography>
                    </InputContainer>
                </Box>
            )}
            {!edit && !editForm && <SingleNumberEditInput value={value} property={property} update={update} />}
        </Stack>
    );
};
