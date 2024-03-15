import { Stack, Typography, TextField } from "@mui/material";
import { t } from "i18next";
import { InputContainer } from "../InputContainer";
type Props = {
    houseblockName: string;
    upDateHouseBlockName: (name: string) => void;
    edit: boolean;
    editForm: boolean;
};
type NameInputProps = {
    houseblockName: string;
    upDateHouseBlockName: (name: string) => void;
};
const NameEditInput = ({ houseblockName, upDateHouseBlockName }: NameInputProps) => {
    return (
        <TextField
            id="name"
            size="small"
            variant="outlined"
            value={houseblockName ? houseblockName : ""}
            onChange={(e) => upDateHouseBlockName(e.target.value)}
            fullWidth
        />
    );
};
export const NameInput = ({ houseblockName, upDateHouseBlockName, edit, editForm }: Props) => {
    return (
        <Stack width="100%">
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.houseBlocksForm.nameLabel")}
            </Typography>
            {edit && editForm && <NameEditInput houseblockName={houseblockName} upDateHouseBlockName={upDateHouseBlockName} />}
            {!edit && editForm && (
                <InputContainer>
                    <Typography>{houseblockName ? houseblockName : ""}</Typography>
                </InputContainer>
            )}
            {!edit && !editForm && <NameEditInput houseblockName={houseblockName} upDateHouseBlockName={upDateHouseBlockName} />}
        </Stack>
    );
};
