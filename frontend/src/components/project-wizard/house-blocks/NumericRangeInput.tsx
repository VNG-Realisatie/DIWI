import { TextField } from "@mui/material";
import React, { useEffect, useState } from "react";

export type ValueType = {
    value: null | number;
    min: null | number;
    max: null | number;
};
type Props = {
    value: ValueType;
    updateCallBack: (value: ValueType) => void;
    labelText?: string;
};

const formatValue = (val: number | null) => {
    return val !== null ? String(val) : "";
};

function fromRangeToString(value: ValueType) {
    let inputValue = "";
    if (value.value !== null) {
        inputValue = formatValue(value.value);
    } else if (value.min !== null || value.max !== null) {
        inputValue = `${formatValue(value.min)}-${formatValue(value.max)}`;
    }
    return inputValue;
}

function fromStringToRange(stringValue: string) {
    const inputValue = stringValue;
    if (inputValue === "") {
        return { value: null, min: null, max: null };
    } else if (inputValue.includes("-")) {
        const [minStr, maxStr] = inputValue.split("-");
        const min = parseFloat(minStr);
        const max = parseFloat(maxStr);

        if (!isNaN(min) && !isNaN(max)) {
            return { value: null, min, max };
        } else if (!isNaN(min)) {
            return { value: null, min, max: null };
        } else if (!isNaN(max)) {
            // We only support open ended ranges with min value. Treat open ended range with max as value
            return { value: max, min: null, max: null };
        } else {
            return { value: null, min: null, max: null };
        }
    } else {
        const newValue = parseFloat(inputValue);
        return { value: newValue, min: null, max: null };
    }
}

export const NumericRangeInput = ({ labelText, value, updateCallBack }: Props) => {
    const [stringValue, setStringValue] = useState<string>(fromRangeToString(value));

    useEffect(() => {
        setStringValue(fromRangeToString(value));
    }, [value]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setStringValue(e.target.value);
    };

    const onBlur = () => {
        const range = fromStringToRange(stringValue);
        updateCallBack(range);
    };

    function handleKey(event: React.KeyboardEvent<HTMLDivElement>): void {
        if (event.key === "Enter") {
            onBlur();
        }
    }
    return (
        <TextField
            id="size"
            size="small"
            variant="outlined"
            label={labelText}
            value={stringValue}
            onChange={handleChange}
            onBlur={onBlur}
            onKeyUp={handleKey}
        />
    );
};
