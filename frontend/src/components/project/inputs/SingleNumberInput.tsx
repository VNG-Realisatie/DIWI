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
    acceptsDecimal?: boolean;
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
    acceptsDecimal = false,
}: SingleNumberEdit) => {
    const hasError = mandatory && (value === null || (value !== null && value < 0));

    const min = 0;
    const max = acceptsDecimal ? 99999999.99 : 99999999;

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const inputValue = e.target.value;
        let newValue: number | null = null;

        if (inputValue === "") {
            newValue = min;
        } else {
            newValue = acceptsDecimal ? parseFloat(parseFloat(inputValue).toFixed(2)) : parseInt(inputValue, 10);
        }

        if (newValue <= max && newValue >= min && !isNaN(newValue)) onChange(newValue);
    };

    const inputField = (
        <TextField
            label={placeholder}
            required={mandatory}
            disabled={readOnly}
            sx={{
                flex: 1,
                backgroundColor: readOnly || hasError ? "inherit" : "#FFFFFF",
            }}
            InputProps={{
                inputProps: {
                    min: min,
                    max: max,
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
            value={value?.toString()}
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
