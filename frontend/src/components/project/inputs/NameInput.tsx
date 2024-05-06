import { TextField, Typography } from "@mui/material";
import { InputContainer } from "../../project-wizard/house-blocks/InputContainer";
import InputLabelStack from "./InputLabelStack";

type Props = {
    value: string;
    setValue: any;
    nullable?: boolean; //not implemented
    readOnly: boolean;
    mandatory: boolean;
    errorText?: string;
    title?: string;
};

const shouldDisplayError = (mandatory: boolean, value: string) => {
    return mandatory && (!value || value.trim() === "");
};

const NameInput = ({ value, setValue, readOnly, mandatory, errorText, title }: Props) => {
    const hasError = shouldDisplayError(mandatory, value);
    return (
        <InputLabelStack mandatory={mandatory} title={title || ""}>
            {!readOnly ? (
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
            ) : (
                <InputContainer>
                    <Typography>{value ?? ""}</Typography>
                </InputContainer>
            )}
        </InputLabelStack>
    );
};

export default NameInput;
