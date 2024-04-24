import { Stack, Typography } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import { t } from "i18next";
import dayjs, { Dayjs } from "dayjs";
import { InputContainer } from "../InputContainer";
import { convertDayjsToString } from "../../../../utils/convertDayjsToString";
import { LabelComponent } from "../../../project/LabelComponent";
import { DateValidationErrors } from "../../../../pages/ProjectWizardBlocks";
type Props = {
    houseBlockStartDate: string | null;
    updateHouseBlockStartDate: (date: Dayjs | null) => void;
    readOnly: boolean;
    errors: DateValidationErrors;
};
export const StartDatePicker = ({ houseBlockStartDate, updateHouseBlockStartDate, readOnly, errors }: Props) => {
    return (
        <Stack width="100%">
            <LabelComponent required text={t("createProject.houseBlocksForm.startDate")} />

            {!readOnly && (
                <DatePicker
                    value={houseBlockStartDate ? dayjs(houseBlockStartDate) : null}
                    onChange={updateHouseBlockStartDate}
                    slotProps={{
                        textField: {
                            variant: "outlined",
                            error: errors.startDateError ? true : false,
                            helperText: errors.startDateError,
                        },
                    }}
                />
            )}
            {readOnly && (
                <InputContainer>
                    <Typography>{convertDayjsToString(dayjs(houseBlockStartDate))}</Typography>
                </InputContainer>
            )}
        </Stack>
    );
};
