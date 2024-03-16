import { Stack, Typography } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import { t } from "i18next";
import dayjs, { Dayjs } from "dayjs";
import { InputContainer } from "../InputContainer";
type Props = {
    houseBlockEndDate: string | null;
    updateHouseBlockEndDate: (date: Dayjs | null) => void;
    edit: boolean;
    editForm: boolean;
};
export const EndDatePicker = ({ houseBlockEndDate, updateHouseBlockEndDate, edit, editForm }: Props) => {
    return (
        <Stack width="100%">
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.houseBlocksForm.endDate")}
            </Typography>
            {edit && editForm && <DatePicker value={houseBlockEndDate ? dayjs(houseBlockEndDate) : null} onChange={updateHouseBlockEndDate} />}
            {!edit && editForm && (
                <InputContainer>
                    <Typography>{houseBlockEndDate}</Typography>
                </InputContainer>
            )}
            {!edit && !editForm && (
                <DatePicker format="DD-MM-YYYY" value={houseBlockEndDate ? dayjs(houseBlockEndDate) : null} onChange={updateHouseBlockEndDate} />
            )}
        </Stack>
    );
};
