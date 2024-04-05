import { Stack, Typography, TextField } from "@mui/material";
import { t } from "i18next";
import { InputContainer } from "../InputContainer";
import { LabelComponent } from "../../../project/LabelComponent";
type Props = {
    houseblockName: string;
    upDateHouseBlockName: (name: string) => void;
    readOnly: boolean;
};
type NameInputProps = {
    houseblockName: string;
    upDateHouseBlockName: (name: string) => void;
};
const NameEditInput = ({ houseblockName, upDateHouseBlockName }: NameInputProps) => {
    return (
        <TextField
            required
            id="name"
            size="small"
            variant="outlined"
            value={houseblockName ? houseblockName : ""}
            onChange={(e) => upDateHouseBlockName(e.target.value)}
            fullWidth
            error={!houseblockName || houseblockName === ""}
            helperText={!houseblockName || houseblockName === "" ? t("createProject.nameIsRequried") : ""}
        />
    );
};
export const NameInput = ({ houseblockName, upDateHouseBlockName, readOnly }: Props) => {
    return (
        <Stack width="100%">
            <LabelComponent required text={t("createProject.houseBlocksForm.nameLabel")} />
            {!readOnly && <NameEditInput houseblockName={houseblockName} upDateHouseBlockName={upDateHouseBlockName} />}
            {readOnly && (
                <InputContainer>
                    <Typography>{houseblockName ? houseblockName : ""}</Typography>
                </InputContainer>
            )}
        </Stack>
    );
};
