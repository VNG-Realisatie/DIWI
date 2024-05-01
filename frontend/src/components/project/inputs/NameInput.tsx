import { Box, TextField, Typography } from "@mui/material";
import { InputContainer } from "../../project-wizard/house-blocks/InputContainer";

type Props = {
    title?: string;
    value: string;
    setValue: any;
    nullable?: boolean;
    readOnly: boolean;
    mandatory: boolean;
    errorText?: string;
};

const NameInput = ({ title, value, setValue, nullable, readOnly, mandatory, errorText }: Props) => {
    return (
        <Box>
            {!readOnly && (
                <TextField
                    sx={{ width: "100%" }}
                    size="small"
                    variant="outlined"
                    value={value ?? ""}
                    onChange={setValue}
                    error={mandatory && !value}
                    helperText={mandatory && !value ? errorText : ""}
                />
            )}
            {readOnly && (
                <InputContainer>
                    <Typography>{value ?? ""}</Typography>
                </InputContainer>
            )}
        </Box>
    );
};

export default NameInput;
