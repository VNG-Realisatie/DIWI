import { Stack, TextField, Typography } from "@mui/material";
import { InputContainer } from "../../project-wizard/house-blocks/InputContainer";
import { LabelComponent } from "../LabelComponent";

type Props = {
    title?: string;
    value: string;
    setValue: any;
    nullable?: boolean;
    readOnly: boolean;
    mandatory: boolean;
    errorText?: string;
    label?: string;
};

const shouldDisplayError = (mandatory: boolean, value: string) => {
    return mandatory && (!value || value.trim() === "");
};

const NameInput = ({ title, value, setValue, nullable, readOnly, mandatory, errorText, label }: Props) => {
    const hasError = shouldDisplayError(mandatory, value);
    return (
        <Stack width="100%">
            {label && <LabelComponent required={mandatory} text={label} />}
            {!readOnly && (
                <TextField
                    required={mandatory}
                    sx={{ width: "100%" }}
                    size="small"
                    variant="outlined"
                    value={value ?? ""}
                    onChange={setValue}
                    error={hasError}
                    helperText={hasError ? errorText : ""}
                />
            )}
            {readOnly && (
                <InputContainer>
                    <Typography>{value ?? ""}</Typography>
                </InputContainer>
            )}
        </Stack>
    );
};

export default NameInput;
