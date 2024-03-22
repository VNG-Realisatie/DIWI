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
        if (e.target.value.includes("-")) {
            const values = e.target.value.split("-");
            const newSize = {
                value: null,
                min: parseFloat(values[0]),
                max: parseFloat(values[1]),
            };
            updateCallBack(newSize);
        } else {
            const newSize = {
                value: +e.target.value,
                min: null,
                max: null,
            };
            updateCallBack(newSize);
        }
    };
    return (
        <TextField
            id="size"
            size="small"
            variant="outlined"
            label={labelText}
            value={value?.value !== null ? value?.value : value?.min + "-" + value?.max}
            onChange={handleSizeChange}
        />
    );
};
