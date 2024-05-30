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
    type?: string;
};

const shouldDisplayError = (mandatory: boolean, value: string, type: string) => {
    if (mandatory && (!value || value.trim() === "")) {
        return true;
    }

    if (type === "email") {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(value)) {
            return true;
        }
    }

    if (type === "tel") {
        const telRegex = /^\+?[1-9]\d{1,14}$/;
        if (!telRegex.test(value)) {
            return true;
        }
    }

    return false;
};

const TextInput = ({ value, setValue, readOnly, mandatory, errorText, title, type = "text" }: Props) => {
    const hasError = shouldDisplayError(mandatory, value, type);

    return (
        <InputLabelStack mandatory={mandatory} title={title || ""}>
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
                type={type}
            />
        </InputLabelStack>
    );
};

export default TextInput;
