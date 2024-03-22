import { Stack, Typography } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import { t } from "i18next";
import dayjs, { Dayjs } from "dayjs";
import { InputContainer } from "../InputContainer";
import { convertDayjsToString } from "../../../../utils/convertDayjsToString";
import { LabelComponent } from "../../../project/LabelComponent";
type Props = {
    houseBlockStartDate: string | null;
    updateHouseBlockStartDate: (date: Dayjs | null) => void;
    edit: boolean;
    editForm: boolean;
};
export const StartDatePicker = ({ houseBlockStartDate, updateHouseBlockStartDate, edit, editForm }: Props) => {
    return (
        <Stack width="100%">
            <LabelComponent required text={t("createProject.houseBlocksForm.startDate")} />

            {edit && editForm && <DatePicker value={houseBlockStartDate ? dayjs(houseBlockStartDate) : null} onChange={updateHouseBlockStartDate} />}
            {!edit && editForm && (
                <InputContainer>
                    <Typography>{convertDayjsToString(dayjs(houseBlockStartDate))}</Typography>
                </InputContainer>
            )}
            {!edit && !editForm && (
                <DatePicker
                    sx={{
                        "& .MuiFormHelperText-root": {
                            color: "red",
                        },
                    }}
                    slotProps={{
                        textField: { helperText: houseBlockStartDate === null ? t("createProject.hasMissingRequiredAreas.startDate") : "" },
                    }}
                    value={houseBlockStartDate ? dayjs(houseBlockStartDate) : null}
                    onChange={updateHouseBlockStartDate}
                />
            )}
        </Stack>
    );
};
