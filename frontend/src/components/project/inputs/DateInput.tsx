import { Stack, Typography } from "@mui/material";
import { InputContainer } from "../../project-wizard/house-blocks/InputContainer";
import { LabelComponent } from "../LabelComponent";
import { DatePicker } from "@mui/x-date-pickers";
import dayjs from "dayjs";
import { convertDayjsToString } from "../../../utils/convertDayjsToString";

type Props = {
    value: string | null;
    setValue: any;
    nullable?: boolean; //not implemented
    readOnly: boolean;
    mandatory: boolean;
    error?: string | undefined | null;
    label?: string;
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
const DateInput = ({ value, setValue, readOnly, mandatory, error, label, errorText }: Props) => {
    const errorMessage = getDateErrorMessage(error, value, errorText);
    return (
        <Stack width="100%">
            {label && <LabelComponent required={mandatory} text={label} />}
            {!readOnly && (
                <DatePicker
                    value={value ? dayjs(value) : null}
                    onChange={setValue}
                    slotProps={{
                        textField: {
                            variant: "outlined",
                            error: errorMessage ? true : false,
                            helperText: errorMessage,
                        },
                    }}
                />
            )}
            {readOnly && (
                <InputContainer>
                    <Typography>{convertDayjsToString(dayjs(value))}</Typography>
                </InputContainer>
            )}
        </Stack>
    );
};

export default DateInput;
