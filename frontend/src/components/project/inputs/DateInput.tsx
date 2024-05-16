import { DatePicker } from "@mui/x-date-pickers";
import dayjs, { Dayjs } from "dayjs";
import { dateFormats } from "../../../localization";
import InputLabelStack from "./InputLabelStack";

type Props = {
    value: string | null;
    setValue: (value: Dayjs | null) => void;
    nullable?: boolean; //not implemented
    readOnly: boolean;
    mandatory: boolean;
    error?: string | undefined | null;
    title?: string;
    errorText: string;
};

const getDateErrorMessage = (error: string | undefined | null, value: string | null, errorText: string): string | null => {
    if (error) {
        return error;
    }
    if (!value) {
        return errorText;
    }
    return null;
};
const DateInput = ({ value, setValue, readOnly, mandatory, error, title, errorText }: Props) => {
    const errorMessage = getDateErrorMessage(error, value, errorText);
    return (
        <InputLabelStack mandatory={mandatory} title={title || ""}>
            <DatePicker
                format={dateFormats.keyboardDate}
                value={value ? dayjs(value) : null}
                onChange={setValue}
                disabled={readOnly}
                slotProps={{
                    textField: {
                        size: "small",
                        fullWidth: true,
                        variant: "outlined",
                        error: errorMessage ? true : false,
                        helperText: errorMessage,
                    },
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

export default DateInput;
