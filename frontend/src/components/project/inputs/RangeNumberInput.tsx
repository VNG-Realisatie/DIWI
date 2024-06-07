import React, { useEffect, useState } from "react";
import { InputAdornment, TextField, Typography } from "@mui/material";
import InputLabelStack from "./InputLabelStack";
import EuroIcon from "@mui/icons-material/Euro";

export type ValueType = {
    value: null | number;
    min: null | number;
    max: null | number;
};

type Props = {
    value: ValueType;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    updateCallBack: (value: any) => void;
    labelText?: string;
    isMonetary?: boolean;
    readOnly: boolean;
    mandatory: boolean;
    title?: string;
};

const decimalSeparator = ",";

function formatValue(val: number | null): string {
    return val !== null ? String(val) : "";
}

function formatMonetaryValue(val: number | null) {
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
    if (inputValue === null) {
        return { value: null, min: null, max: null };
    } else if (inputValue.includes("-")) {
        const [minStr, maxStr] = inputValue.split("-");
        const min = isMonetary ? parseMonetary(minStr) : parseFloat(minStr);
        const max = isMonetary ? parseMonetary(maxStr) : parseFloat(maxStr);

        if (!isNaN(min) && !isNaN(max)) {
            return { value: null, min, max };
        } else if (!isNaN(min)) {
            return { value: null, min, max: null };
        } else if (!isNaN(max)) {
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

const RangeNumberInput = ({ labelText, value, updateCallBack, isMonetary = false, readOnly, mandatory, title }: Props) => {
    const [stringValue, setStringValue] = useState<string | null>(null);

    useEffect(() => {
        setStringValue(fromRangeToString(value, isMonetary));
    }, [value, isMonetary]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newValue = e.target.value;
        const isValidInput = /^-?\d*(,\d{0,2})?(-\d*(,?\d{0,2})?)?$/.test(newValue);

        if (isValidInput) setStringValue(newValue);
    };

    const handleFocus = () => {
        if (stringValue === "0,00") setStringValue(null);
    };

    const handleBlur = () => {
        if (isMonetary) {
            if (stringValue === null) return;
        }
        const range = fromStringToRange(stringValue, isMonetary);
        stringValue ?? updateCallBack(range);
    };

    function handleKey(event: React.KeyboardEvent<HTMLDivElement>): void {
        if (event.key === "Enter") {
            handleBlur();
        }
    }

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
                        transform: isMonetary ? "translate(34px, 9px)" : null,
                    },
                }}
                value={stringValue ?? ""}
                onChange={handleChange}
                onBlur={handleBlur}
                onFocus={handleFocus}
                onKeyUp={handleKey}
                InputProps={{
                    startAdornment: isMonetary && (
                        <InputAdornment position="start" sx={{ pointerEvents: "none" }}>
                            <EuroIcon fontSize="inherit" />
                        </InputAdornment>
                    ),
                    endAdornment: (
                        <InputAdornment onClick={() => setStringValue(null)} position="end" sx={{ ":hover": { cursor: "pointer" } }}>
                            <Typography fontStyle="italic" fontSize={12} sx={{ textDecoration: "underline" }}>
                                Leeg maken
                            </Typography>
                        </InputAdornment>
                    ),
                }}
                sx={{
                    "& .MuiInputBase-input.Mui-disabled": {
                        backgroundColor: "#0000",
                    },
                    "& .MuiInputBase-adornedEnd": { backgroundColor: "white" },
                    "& .MuiInputAdornment-positionStart": { marginRight: "2px" },
                }}
            />
        </InputLabelStack>
    );
};

export default RangeNumberInput;
