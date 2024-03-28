import { Stack, Typography } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import { t } from "i18next";
import dayjs, { Dayjs } from "dayjs";
import { InputContainer } from "../InputContainer";
import { convertDayjsToString } from "../../../../utils/convertDayjsToString";
import { LabelComponent } from "../../../project/LabelComponent";

type Props = {
    houseBlockEndDate: string | null;
    updateHouseBlockEndDate: (date: Dayjs | null) => void;
    readOnly: boolean;
};

export const EndDatePicker = ({ houseBlockEndDate, updateHouseBlockEndDate, readOnly }: Props) => {
    return (
        <Stack width="100%">
            <LabelComponent required text={t("createProject.houseBlocksForm.endDate")} />

            {!readOnly && <DatePicker value={houseBlockEndDate ? dayjs(houseBlockEndDate) : null} onChange={updateHouseBlockEndDate} />}
            {readOnly && (
                <InputContainer>
                    <Typography>{convertDayjsToString(dayjs(houseBlockEndDate))}</Typography>
                </InputContainer>
            )}
        </Stack>
    );
};
