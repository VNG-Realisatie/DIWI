import { Stack, TextField, Typography } from "@mui/material";
import { t } from "i18next";
import { InputContainer } from "../InputContainer";
export type HouseBlockSize = {
    value: number | null;
    min: number | null;
    max: number | null;
};
type Props = {
    houseBlockSize: HouseBlockSize;
    updateHouseBlockSize: (houseBlockSize: HouseBlockSize) => void;
    edit: boolean;
    editForm: boolean;
};
type HouseBlockSizeProps = {
    houseBlockSize: HouseBlockSize;
    updateHouseBlockSize: (houseBlockSize: HouseBlockSize) => void;
};
const HouseBlockSizeEditInput = ({ houseBlockSize, updateHouseBlockSize }: HouseBlockSizeProps) => {
    const handleSizeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newSize = {
            value: +e.target.value,
            min: null,
            max: null,
        };

        updateHouseBlockSize(newSize);
    };
    return (
        <TextField
            InputProps={{
                inputProps: {
                    min: 0,
                },
            }}
            type="number"
            id="size"
            size="small"
            variant="outlined"
            value={houseBlockSize?.value !== null ? houseBlockSize?.value : ""}
            onChange={handleSizeChange}
        />
    );
};
export const SizeInput = ({ houseBlockSize, updateHouseBlockSize, edit, editForm }: Props) => {
    return (
        <Stack width="100%">
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.houseBlocksForm.size")}
            </Typography>
            {edit && editForm && <HouseBlockSizeEditInput houseBlockSize={houseBlockSize} updateHouseBlockSize={updateHouseBlockSize} />}
            {!edit && editForm && (
                <InputContainer>
                    <Typography>{houseBlockSize?.value}</Typography>
                </InputContainer>
            )}
            {!edit && !editForm && <HouseBlockSizeEditInput houseBlockSize={houseBlockSize} updateHouseBlockSize={updateHouseBlockSize} />}
        </Stack>
    );
};
