import { Stack, Typography } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import { t } from "i18next";
import dayjs, { Dayjs } from "dayjs";
import { InputContainer } from "../InputContainer";
import { convertDayjsToString } from "../../../../utils/convertDayjsToString";
type Props = {
    houseBlockStartDate: string | null;
    updateHouseBlockStartDate: (date: Dayjs | null) => void;
    edit: boolean;
    editForm: boolean;
};
export const StartDatePicker = ({ houseBlockStartDate, updateHouseBlockStartDate, edit, editForm }: Props) => {
    return (
        <Stack width="100%">
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.houseBlocksForm.startDate")}
            </Typography>
            {edit && editForm && <DatePicker value={houseBlockStartDate ? dayjs(houseBlockStartDate) : null} onChange={updateHouseBlockStartDate} />}
            {!edit && editForm && (
                <InputContainer>
                    <Typography>{convertDayjsToString(dayjs(houseBlockStartDate))}</Typography>
                </InputContainer>
            )}
            {!edit && !editForm && <DatePicker value={houseBlockStartDate ? dayjs(houseBlockStartDate) : null} onChange={updateHouseBlockStartDate} />}
        </Stack>
    );
};
