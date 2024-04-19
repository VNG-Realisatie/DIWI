import { Stack, Typography } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import { t } from "i18next";
import dayjs, { Dayjs } from "dayjs";
import { InputContainer } from "../InputContainer";
import { convertDayjsToString } from "../../../../utils/convertDayjsToString";
import { LabelComponent } from "../../../project/LabelComponent";
import { DateValidationErrors } from "../../../../pages/ProjectWizardBlocks";

type Props = {
    houseBlockEndDate: string | null;
    updateHouseBlockEndDate: (date: Dayjs | null) => void;
    readOnly: boolean;
    errors: DateValidationErrors;
};

export const EndDatePicker = ({ houseBlockEndDate, updateHouseBlockEndDate, readOnly, errors }: Props) => {
    return (
        <Stack width="100%">
            <LabelComponent required text={t("createProject.houseBlocksForm.endDate")} />

            {!readOnly && (
                <DatePicker
                    value={houseBlockEndDate ? dayjs(houseBlockEndDate) : null}
                    onChange={updateHouseBlockEndDate}
                    slotProps={{
                        textField: {
                            variant: "outlined",
                            error: errors.endDateError ? true : false,
                            helperText: errors.endDateError,
                        },
                    }}
                />
            )}
            {readOnly && (
                <InputContainer>
                    <Typography>{convertDayjsToString(dayjs(houseBlockEndDate))}</Typography>
                </InputContainer>
            )}
        </Stack>
    );
};
