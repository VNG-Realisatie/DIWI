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
    edit: boolean;
    editForm: boolean;
};

export const EndDatePicker = ({ houseBlockEndDate, updateHouseBlockEndDate, edit, editForm }: Props) => {
    return (
        <Stack width="100%">
            <LabelComponent required text={t("createProject.houseBlocksForm.endDate")} />

            {edit && editForm && <DatePicker value={houseBlockEndDate ? dayjs(houseBlockEndDate) : null} onChange={updateHouseBlockEndDate} />}
            {!edit && editForm && (
                <InputContainer>
                    <Typography>{convertDayjsToString(dayjs(houseBlockEndDate))}</Typography>
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
                        textField: { helperText: houseBlockEndDate === null ? t("createProject.hasMissingRequiredAreas.endDate") : "" },
                    }}
                    value={houseBlockEndDate ? dayjs(houseBlockEndDate) : null}
                    onChange={updateHouseBlockEndDate}
                />
            )}
        </Stack>
    );
};
