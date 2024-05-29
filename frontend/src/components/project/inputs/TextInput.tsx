import { TextField } from "@mui/material";
import InputLabelStack from "./InputLabelStack";

type Props = {
    value: string;
    setValue: any;
    nullable?: boolean; //not implemented
    readOnly: boolean;
    mandatory: boolean;
    errorText?: string;
    title?: string;
    tooltipInfoText?: string;
};

const shouldDisplayError = (mandatory: boolean, value: string) => {
    return mandatory && (!value || value.trim() === "");
};

const TextInput = ({ value, setValue, readOnly, mandatory, errorText, title, tooltipInfoText }: Props) => {
    const hasError = shouldDisplayError(mandatory, value);
    return (
        <InputLabelStack mandatory={mandatory} title={title || ""} tooltipInfoText={tooltipInfoText}>
            <TextField
                required={mandatory}
                sx={{
                    width: "100%",
                    "& .MuiInputBase-input.Mui-disabled": {
                        backgroundColor: "transparent",
                    },
                }}
                size="small"
                variant="outlined"
                value={value ?? ""}
                onChange={setValue}
                error={hasError}
                helperText={hasError ? errorText : ""}
                disabled={readOnly}
            />
        </InputLabelStack>
    );
};

export default TextInput;
