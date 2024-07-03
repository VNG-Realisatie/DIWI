import { TextField } from "@mui/material";
import InputLabelStack from "./InputLabelStack";
import { validateEmail } from "../../../utils/emailValidation";

type Props = {
    value: string;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    setValue: any;
    nullable?: boolean; //not implemented
    readOnly: boolean;
    mandatory: boolean;
    errorText?: string;
    title?: string;
    type?: string;
    tooltipInfoText?: string;
};

const shouldDisplayError = (mandatory: boolean, value: string, type: string) => {
    if (mandatory && (!value || value.trim() === "")) {
        return true;
    }

    if (type === "email") {
        if (!validateEmail(value)) {
            return true;
        }
    }
    return false;
};

const TextInput = ({ value, setValue, readOnly, mandatory, errorText, title, tooltipInfoText, type = "text" }: Props) => {
    const hasError = shouldDisplayError(mandatory, value, type);
    return (
        <InputLabelStack mandatory={mandatory} title={title || ""} tooltipInfoText={tooltipInfoText}>
            <TextField
                required={mandatory}
                sx={{
                    width: "100%",
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
