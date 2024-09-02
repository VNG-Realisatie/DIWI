import React, { useEffect, useState } from "react";
import { InputAdornment, TextField } from "@mui/material";
import InputLabelStack from "./InputLabelStack";
import EuroIcon from "@mui/icons-material/Euro";
import ClearInputAdornment from "./ClearInputAdornment";

export type ValueType = {
    value: null | number;
    min: null | number;
    max: null | number;
};

type Props = {
    value: ValueType;
    updateCallBack: (value: ValueType) => void;
    labelText?: string;
    isMonetary?: boolean;
    readOnly: boolean;
    mandatory: boolean;
    title?: string;
    errorText?: string;
    setIsRangeValid?: (isValid: boolean) => void;
    displayError?: boolean;
};

const decimalSeparator = ",";

function formatValue(val: number | null): string {
    return val !== null ? String(val) : "";
}

export function formatMonetaryValue(val: number | null) {
    if (val === null) {
        return "";
    } else {
        const euros = Math.floor(val / 100);
        const cents = val % 100;
        return `${euros}${decimalSeparator}${cents.toString().padStart(2, "0")}`;
    }
}

function parseMonetary(value: string) {
    const [euros, cents] = value.split(decimalSeparator);
    let result = parseInt(euros) * 100;
    if (cents !== undefined) {
        let parsedCents = parseInt(cents);
        if (cents.length === 1) {
            parsedCents *= 10;
        }
        result += parsedCents;
    }
    return result;
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
function fromMonetaryToString(value: any): string {
    let inputValue = "";
    if (value.value !== null) {
        inputValue = formatMonetaryValue(value.value);
    } else if (value.min !== null || value.max !== null) {
        inputValue = `${formatMonetaryValue(value.min)}-${formatMonetaryValue(value.max)}`;
    }
    return inputValue;
}

function fromRangeToString(value: ValueType, isMonetary: boolean): string {
    if (isMonetary) {
        return fromMonetaryToString(value);
    } else {
        if (value.value !== null) {
            return formatValue(value.value);
        } else if (value.min !== null || value.max !== null) {
            return `${formatValue(value.min)}-${formatValue(value.max)}`;
        }
        return "";
    }
}

function fromStringToRange(stringValue: string | null, isMonetary: boolean): ValueType {
    const inputValue = stringValue;
    if (inputValue === null || inputValue === "") {
        return { value: null, min: null, max: null };
    } else if (inputValue.includes("-")) {
        const [minStr, maxStr] = inputValue.split("-");
        const min = minStr ? (isMonetary ? parseMonetary(minStr) : parseFloat(minStr)) : null;
        const max = maxStr ? (isMonetary ? parseMonetary(maxStr) : parseFloat(maxStr)) : null;

        if (min !== null && max !== null) {
            return { value: null, min, max };
        } else if (min !== null) {
            return { value: null, min, max: null };
        } else if (max !== null) {
            // We only support open-ended ranges with min value. Treat open-ended range with max as value
            return { value: max, min: null, max: null };
        } else {
            return { value: null, min: null, max: null };
        }
    } else {
        const newValue = isMonetary ? parseMonetary(inputValue) : parseFloat(inputValue);
        return { value: newValue, min: null, max: null };
    }
}

const shouldDisplayError = (mandatory: boolean, value: string | null, displayError: boolean) => {
    if ((mandatory && !value) || displayError) {
        return true;
    }
    return false;
};

const RangeNumberInput = ({
    labelText,
    value,
    updateCallBack,
    isMonetary = false,
    readOnly,
    mandatory,
    title,
    errorText,
    setIsRangeValid,
    displayError = false,
}: Props) => {
    const [stringValue, setStringValue] = useState<string | null>(null);

    const hasError = shouldDisplayError(mandatory, stringValue, displayError);

    useEffect(() => {
        setStringValue(fromRangeToString(value, isMonetary));
    }, [value, isMonetary]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        let newValue = e.target.value;
        newValue = newValue.replace(".", ",");
        const isValidInput = /^-?\d*(,\d{0,2})?(-\d*(,?\d{0,2})?)?$/.test(newValue);
        const checkRangeValidation = newValue.trim() !== "-" ? newValue.trim() !== "" : false;
        setIsRangeValid && setIsRangeValid(checkRangeValidation);
        if (isValidInput) setStringValue(newValue);
    };

    const handleBlur = () => {
        if (stringValue !== null) {
            const range = fromStringToRange(stringValue, isMonetary);
            updateCallBack(range);
        }
    };

    function handleKey(event: React.KeyboardEvent<HTMLDivElement>): void {
        if (event.key === "Enter") {
            handleBlur();
        }
    }

    const handleClearInput = () => {
        setStringValue("");
        updateCallBack({ value: null, min: null, max: null });
    };

    return (
        <InputLabelStack title={title || ""} mandatory={mandatory}>
            <TextField
                required={mandatory}
                disabled={readOnly}
                id="size"
                size="small"
                variant="outlined"
                label={!stringValue ? (mandatory ? labelText : "Leeg") : ""}
                InputLabelProps={{
                    shrink: false,
                    sx: {
                        "&.Mui-focused": {
                            display: "none",
                        },
                        fontStyle: "italic",
                        color: "rgba(0, 0, 0, 0.3)",
                        transform: isMonetary ? "translate(34px, 12px)" : null,
                    },
                }}
                value={stringValue ?? ""}
                onChange={handleChange}
                onBlur={handleBlur}
                onKeyUp={handleKey}
                InputProps={{
                    startAdornment: isMonetary && (
                        <InputAdornment position="start" sx={{ pointerEvents: "none" }}>
                            <EuroIcon fontSize="inherit" />
                        </InputAdornment>
                    ),
                    endAdornment: !readOnly && !mandatory && <ClearInputAdornment onClick={handleClearInput} />,
                }}
                sx={{
                    "& .MuiInputBase-input.Mui-disabled, & .MuiInputBase-adornedEnd": {
                        backgroundColor: readOnly ? "transparent" : "white",
                    },
                    "& .MuiInputAdornment-positionStart": { marginRight: "2px" },
                }}
                error={hasError}
                helperText={hasError ? errorText : ""}
            />
        </InputLabelStack>
    );
};

export default RangeNumberInput;
