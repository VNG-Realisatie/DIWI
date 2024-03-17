import { Stack, InputLabel, Select, MenuItem, SelectChangeEvent, OutlinedInput, Typography } from "@mui/material";
import { t } from "i18next";
import { mutationSelectOptions } from "../constants";
import { MutationSelectOptions } from "../../../../types/enums";
import { InputContainer } from "../InputContainer";

type Props = {
    houseBlockMutationKind: MutationSelectOptions[] | null;
    updateHouseBlockMutationKind: (event: SelectChangeEvent<MutationSelectOptions[]>) => void;
    edit: boolean;
    editForm: boolean;
};
type MutationKindProps = {
    houseBlockMutationKind: MutationSelectOptions[] | null;
    updateHouseBlockMutationKind: (event: SelectChangeEvent<MutationSelectOptions[]>) => void;
};
const MutationKindEditOption = ({ houseBlockMutationKind, updateHouseBlockMutationKind }: MutationKindProps) => {
    return (
        <Select
            size="small"
            labelId="mutationtype"
            id="fase"
            multiple
            value={houseBlockMutationKind ? houseBlockMutationKind : []}
            label={t("createProject.houseBlocksForm.mutationType")}
            onChange={updateHouseBlockMutationKind}
            input={<OutlinedInput />}
            renderValue={(selected) => selected.join(", ")}
        >
            {mutationSelectOptions.map((m) => {
                return (
                    <MenuItem key={m} value={m}>
                        {m}
                    </MenuItem>
                );
            })}
        </Select>
    );
};

export const MutationKindSelect = ({ houseBlockMutationKind, updateHouseBlockMutationKind, edit, editForm }: Props) => {
    return (
        <Stack>
            <InputLabel id="mutationtype"> {t("createProject.houseBlocksForm.mutationType")}</InputLabel>
            {edit && editForm && (
                <MutationKindEditOption houseBlockMutationKind={houseBlockMutationKind} updateHouseBlockMutationKind={updateHouseBlockMutationKind} />
            )}
            {!edit && editForm && (
                <InputContainer>
                    <Typography>{houseBlockMutationKind?.join(",")}</Typography>
                </InputContainer>
            )}
            {!edit && !editForm && (
                <MutationKindEditOption houseBlockMutationKind={houseBlockMutationKind} updateHouseBlockMutationKind={updateHouseBlockMutationKind} />
            )}
        </Stack>
    );
};
