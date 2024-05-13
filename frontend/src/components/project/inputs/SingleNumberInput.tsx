import { Stack, Typography, TextField } from "@mui/material";
import InputLabelStack from "./InputLabelStack";

type SingleNumberEdit = {
    name?: string;
    value: number | null;
    onChange: any;
    readOnly: boolean;
    mandatory: boolean;
    error?: string;
    isInputLabel?: boolean;
    translationPath?: string;
    nullable?: boolean; //not implemented
    placeholder?: string;
};

export const SingleNumberInput = ({ name, value, onChange, readOnly, mandatory, error, isInputLabel = false, placeholder }: SingleNumberEdit) => {
    const hasError = mandatory && (!value || value <= 0);

    const inputField = (
        <TextField
            label={placeholder}
            required={mandatory}
            disabled={readOnly}
            sx={{ flex: 1 }}
            InputProps={{
                inputProps: {
                    min: 0,
                },
            }}
            type="number"
            id={name ? name : ""}
            size="small"
            variant="outlined"
            value={value ? value : 0}
            onChange={(e) => onChange(+e.target.value)}
            error={hasError}
            helperText={hasError ? error : ""}
        />
    );

    return isInputLabel ? (
        <InputLabelStack title={name || ""} mandatory={mandatory}>
            {inputField}
        </InputLabelStack>
    ) : (
        <Stack direction="row" alignItems="center" spacing={2} my={2}>
            <Typography variant="subtitle1" fontWeight="500" border="solid 1px #ddd" borderRadius="5px" p={0.6} flex={3}>
                {name}
            </Typography>
            {inputField}
        </Stack>
    );
};
