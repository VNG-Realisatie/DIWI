import { Stack, TextField, InputAdornment, Box } from "@mui/material";
import InputLabelStack from "./InputLabelStack";
import { TooltipInfo } from "../../../widgets/TooltipInfo";

type SingleNumberEdit = {
    name?: string;
    value: number | null;
    onChange: (newValue: number | null) => void;
    readOnly: boolean;
    mandatory: boolean;
    error?: string;
    isInputLabel?: boolean;
    translationPath?: string;
    nullable?: boolean; //not implemented
    placeholder?: string;
    isDemolition?: boolean;
    tooltipInfoText?: string;
    acceptsDecimal?: boolean; // new property
};

export const SingleNumberInput = ({
    name,
    value,
    onChange,
    readOnly,
    mandatory,
    error,
    isInputLabel = false,
    placeholder,
    isDemolition = false,
    tooltipInfoText,
    acceptsDecimal = false, // default to false
}: SingleNumberEdit) => {
    const hasError = mandatory && (!value || value <= 0);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newValue = acceptsDecimal ? parseFloat(e.target.value) : parseInt(e.target.value, 10);
        onChange(isNaN(newValue) ? null : newValue);
    };

    const inputField = (
        <TextField
            label={placeholder}
            required={mandatory}
            disabled={readOnly}
            sx={{
                flex: 1,
            }}
            InputProps={{
                inputProps: {
                    min: 0,
                    step: acceptsDecimal ? "0.01" : "1",
                },
                startAdornment: isDemolition && value != 0 && value != null && (
                    <InputAdornment position="start" style={{ marginRight: "0px" }}>
                        -
                    </InputAdornment>
                ),
            }}
            type="number"
            id={name ? name.replace(/\s/g, "") : ""}
            size="small"
            variant="outlined"
            value={value !== null ? value : ""}
            onChange={handleChange}
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
            <Box
                sx={{
                    border: "solid 1px #ddd",
                    borderRadius: "5px",
                    padding: "0.6em",
                    minHeight: "47px",
                    flex: 3,
                    color: readOnly ? "#000000" : "inherit",
                    fontStyle: readOnly ? "italic" : "normal",
                }}
            >
                {name} {tooltipInfoText && <TooltipInfo text={tooltipInfoText} />}
            </Box>
            {inputField}
        </Stack>
    );
};
