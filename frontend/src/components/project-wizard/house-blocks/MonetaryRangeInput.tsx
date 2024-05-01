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

const decimalSeparator = ",";

/** Monetary values are stored as ints in cents. Convert this to displaying euros */
export const formatMonetaryValue = (val: number | null) => {
    if (val === null) {
        return "";
    } else {
        const euros = Math.floor(val / 100);
        const cents = val % 100;
        return `${euros}${decimalSeparator}${cents.toString().padStart(2, "0")}`;
    }
};

function fromRangeToString(value: ValueType) {
    let inputValue = "";
    if (value.value !== null) {
        inputValue = formatMonetaryValue(value.value);
    } else if (value.min !== null || value.max !== null) {
        inputValue = `${formatMonetaryValue(value.min)}-${formatMonetaryValue(value.max)}`;
    }
    return inputValue;
}

const parseMonetary = (value: string) => {
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
};

function fromStringToRange(stringValue: string) {
    const inputValue = stringValue;
    if (inputValue === "") {
        return { value: null, min: null, max: null };
    } else if (inputValue.includes("-")) {
        const [minStr, maxStr] = inputValue.split("-");
        const min = parseMonetary(minStr);
        const max = parseMonetary(maxStr);

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
        const newValue = parseMonetary(inputValue);
        return { value: newValue, min: null, max: null };
    }
}

export const MonetaryRangeInput = ({ labelText, value, updateCallBack }: Props) => {
    const [stringValue, setStringValue] = useState<string>(fromRangeToString(value));

    useEffect(() => {
        setStringValue(fromRangeToString(value));
    }, [value]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newValue = e.target.value;
        const isValidInput = /^-?\d*(,\d{0,2})?(-\d*(,?\d{0,2})?)?$/.test(newValue);
        /*
            ^ = Start of the string
            -? = Zero or one hyphen
            \d* = Zero or more digits
            (,\d{0,2})? = 0 to 1 comma followed by 0 to 2 digits
            (-\d*(,?\d{0,2})?)? = 0 to 1 hyphen followed by 0 or more digits, optionally followed by a comma and 0 to 2 digits
            $ End of string
        */

        if (isValidInput) setStringValue(newValue);
    };

    const onFocus = () => {
        if (stringValue === "0,00") setStringValue("");
    };

    const onBlur = () => {
        if (stringValue === "") return setStringValue("0,00");

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
            prefix="€"
            label={labelText}
            value={stringValue}
            onChange={handleChange}
            onBlur={onBlur}
            onFocus={onFocus}
            onKeyUp={handleKey}
        />
    );
};
