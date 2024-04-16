import { Stack, Select, MenuItem, SelectChangeEvent, OutlinedInput, Typography } from "@mui/material";
import { t } from "i18next";
import { MutationKind, mutationKindOptions } from "../../../../types/enums";
import { InputContainer } from "../InputContainer";
import { LabelComponent } from "../../../project/LabelComponent";

type Props = {
    houseBlockMutationKind: MutationKind | undefined;
    updateHouseBlockMutationKind: (event: SelectChangeEvent<MutationKind>) => void;
    readOnly: boolean;
};
type MutationKindProps = {
    houseBlockMutationKind: MutationKind | undefined;
    updateHouseBlockMutationKind: (event: SelectChangeEvent<MutationKind>) => void;
};
const MutationKindEditOption = ({ houseBlockMutationKind, updateHouseBlockMutationKind }: MutationKindProps) => {
    return (
        <Select
            size="small"
            labelId="mutationtype"
            id="fase"
            value={houseBlockMutationKind}
            label={t("createProject.houseBlocksForm.mutationType")}
            onChange={updateHouseBlockMutationKind}
            input={<OutlinedInput />}
        >
            {mutationKindOptions.map((m) => {
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
                    <Typography minHeight="20px">{houseBlockMutationKind}</Typography>
                </InputContainer>
            )}
        </Stack>
    );
};
