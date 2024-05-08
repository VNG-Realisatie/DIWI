import { Stack, Select, MenuItem, SelectChangeEvent, OutlinedInput, Typography, FormHelperText, Box } from "@mui/material";
import { t } from "i18next";
import { MutationKind, mutationKindOptions } from "../../../../types/enums";
import { InputContainer } from "../InputContainer";
import { LabelComponent } from "../../../project/LabelComponent";

type Props = {
    houseBlockMutationKind: MutationKind | null;
    updateHouseBlockMutationKind: (event: SelectChangeEvent<MutationKind | null>) => void;
    readOnly: boolean;
};
type MutationKindProps = {
    houseBlockMutationKind: MutationKind | null;
    updateHouseBlockMutationKind: (event: SelectChangeEvent<MutationKind | null>) => void;
};
const MutationKindEditOption = ({ houseBlockMutationKind, updateHouseBlockMutationKind }: MutationKindProps) => {
    return (
        <>
            <Box display={"flex"}>
                <Select
                    size="small"
                    fullWidth
                    labelId="mutationtype"
                    id="fase"
                    value={houseBlockMutationKind}
                    label={t("createProject.houseBlocksForm.mutationType")}
                    onChange={updateHouseBlockMutationKind}
                    input={<OutlinedInput />}
                    error={!houseBlockMutationKind}
                >
                    {mutationKindOptions.map((m) => {
                        return (
                            <MenuItem key={m} value={m}>
                                {t(`createProject.houseBlocksForm.${m}`)}
                            </MenuItem>
                        );
                    })}
                </Select>
            </Box>
            {!houseBlockMutationKind && (
                <FormHelperText sx={{ paddingLeft: "12px", paddingRight: "15px", color: "#d32f2f" }}>
                    {t("wizard.houseBlocks.mutationKindWarning")}
                </FormHelperText>
            )}
        </>
    );
};

export const MutationKindSelect = ({ houseBlockMutationKind, updateHouseBlockMutationKind, readOnly }: Props) => {
    return (
        <Stack>
            <LabelComponent
                required={false}
                text={t("createProject.houseBlocksForm.mutationType")}
                tooltipInfoText={t("tooltipInfo.mutatieGegevens.mutatieSoort")}
            />

            {!readOnly && (
                <MutationKindEditOption houseBlockMutationKind={houseBlockMutationKind} updateHouseBlockMutationKind={updateHouseBlockMutationKind} />
            )}
            {readOnly && (
                <Box display={"flex"}>
                    <InputContainer>
                        <Typography minHeight="20px"> {houseBlockMutationKind ? t(`createProject.houseBlocksForm.${houseBlockMutationKind}`) : ""}</Typography>
                    </InputContainer>
                </Box>
            )}
        </Stack>
    );
};
