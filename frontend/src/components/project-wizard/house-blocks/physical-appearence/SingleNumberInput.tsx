import { Stack, Typography, TextField, Box } from "@mui/material";
import { t } from "i18next";
import { InputContainer } from "../InputContainer";
type Props = {
    value: string;
    translationPath: string;
    property: number | null;
    update: (propert: number | null) => void;
    readOnly: boolean;
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
            value={property !== null ? property : ""}
            onChange={(e) => update(+e.target.value)}
        />
    );
};
export const SingleNumberInput = ({ property, update, value, translationPath, readOnly }: Props) => {
    return (
        <Stack direction="row" alignItems="center" spacing={2} my={2}>
            <Typography variant="subtitle1" fontWeight="500" border="solid 1px #ddd" borderRadius="5px" p={0.6} flex={3}>
                {t(`${translationPath}.${value}`)}
            </Typography>
            {!readOnly && <SingleNumberEditInput value={value} property={property} update={update} />}
            {readOnly && (
                <Box sx={{ flex: 1 }}>
                    <InputContainer>
                        <Typography minHeight="20px">{property}</Typography>
                    </InputContainer>
                </Box>
            )}
        </Stack>
    );
};
