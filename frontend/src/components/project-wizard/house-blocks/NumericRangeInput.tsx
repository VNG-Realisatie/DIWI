import React from "react";
import { TextField } from "@mui/material";

type ValueType = {
    value: null | number;
    min: null | number;
    max: null | number;
};
type Props = {
    value: ValueType;
    updateCallBack: (value: ValueType) => void;
    labelText?: string;
};

export const NumericRangeInput = ({ labelText, value, updateCallBack }: Props) => {
    const handleSizeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const inputValue = e.target.value.trim();
        if (inputValue === "") {
            updateCallBack({ value: null, min: null, max: null });
        } else if (inputValue.includes("-")) {
            const [minStr, maxStr] = inputValue.split("-");
            const min = isNaN(parseFloat(minStr)) ? null : parseFloat(minStr);
            const max = isNaN(parseFloat(maxStr)) ? null : parseFloat(maxStr);
            updateCallBack({ value: null, min, max });
        } else {
            const newValue = parseFloat(inputValue);
            updateCallBack({ value: newValue, min: null, max: null });
        }
    };

    const formatValue = (val: number | null) => {
        return val !== null ? String(val) : "";
    };

    const inputValue =
        value.value !== null
            ? formatValue(value.value)
            : value.min !== null || value.max !== null
              ? `${formatValue(value.min ?? null)}-${formatValue(value.max ?? null)}`
              : "";

    return <TextField id="size" size="small" variant="outlined" label={labelText} value={inputValue} onChange={handleSizeChange} />;
};
