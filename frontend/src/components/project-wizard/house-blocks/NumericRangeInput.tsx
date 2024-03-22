import { TextField } from "@mui/material";

type ValueType = {
    value: null | number;
    min: null | number;
    max: null | number;
};
type Props = {
    value: ValueType;
    updateCallBack: (value: ValueType) => void;
};

export const NumericRangeInput = ({ value, updateCallBack }: Props) => {
    const handleSizeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.value.includes("-")) {
            const values = e.target.value.split("-");
            const newSize = {
                value: null,
                min: +values[0],
                max: +values[1],
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
            value={value?.value !== null ? value?.value : value?.min + "-" + value?.max}
            onChange={handleSizeChange}
        />
    );
};
