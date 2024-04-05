import { Stack, Select, MenuItem, SelectChangeEvent, OutlinedInput, Typography } from "@mui/material";
import { t } from "i18next";
import { mutationSelectOptions } from "../constants";
import { MutationSelectOptions } from "../../../../types/enums";
import { InputContainer } from "../InputContainer";
import { LabelComponent } from "../../../project/LabelComponent";

type Props = {
    houseBlockMutationKind: MutationSelectOptions[] | null;
    updateHouseBlockMutationKind: (event: SelectChangeEvent<MutationSelectOptions[]>) => void;
    readOnly: boolean;
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

export const MutationKindSelect = ({ houseBlockMutationKind, updateHouseBlockMutationKind, readOnly }: Props) => {
    return (
        <Stack>
            <LabelComponent required={false} text={t("createProject.houseBlocksForm.mutationType")} />

            {!readOnly && (
                <MutationKindEditOption houseBlockMutationKind={houseBlockMutationKind} updateHouseBlockMutationKind={updateHouseBlockMutationKind} />
            )}
            {readOnly && (
                <InputContainer>
                    <Typography minHeight="20px">{houseBlockMutationKind?.join(",")}</Typography>
                </InputContainer>
            )}
        </Stack>
    );
};
