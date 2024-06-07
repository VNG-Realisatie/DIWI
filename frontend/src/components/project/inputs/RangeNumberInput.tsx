import React, { useEffect, useState } from "react";
import { TextField } from "@mui/material";
import InputLabelStack from "./InputLabelStack";

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

function fromStringToRange(stringValue: string, isMonetary: boolean): ValueType {
    const inputValue = stringValue;
    if (inputValue === "") {
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
    const [stringValue, setStringValue] = useState<string>("");

    useEffect(() => {
        setStringValue(fromRangeToString(value, isMonetary));
    }, [value, isMonetary]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newValue = e.target.value;
        const isValidInput = /^-?\d*(,\d{0,2})?(-\d*(,?\d{0,2})?)?$/.test(newValue);

        if (isValidInput) setStringValue(newValue);
    };

    const handleFocus = () => {
        if (stringValue === "0,00") setStringValue("");
    };

    const handleBlur = () => {
        if (isMonetary) {
            if (stringValue === "") return setStringValue("0,00");
        }
        const range = fromStringToRange(stringValue, isMonetary);
        updateCallBack(range);
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
                label={labelText}
                value={stringValue}
                onChange={handleChange}
                onBlur={handleBlur}
                onFocus={handleFocus}
                onKeyUp={handleKey}
                InputProps={{
                    startAdornment: isMonetary ? "€" : "",
                }}
                sx={{
                    "& .MuiInputBase-input.Mui-disabled": {
                        backgroundColor: "#0000",
                    },
                }}
            />
        </InputLabelStack>
    );
};

export default RangeNumberInput;
