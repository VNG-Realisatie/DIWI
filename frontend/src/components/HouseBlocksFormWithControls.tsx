import { Box, Stack, Tooltip } from "@mui/material";
import { HouseBlock } from "./project-wizard/house-blocks/types";
import { useState } from "react";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import ClearIcon from "@mui/icons-material/Clear";
import { t } from "i18next";
import { HouseBlocksForm } from "./HouseBlocksForm";

type Props = {
    houseBlock: HouseBlock;
    setHouseBlock: (hb: HouseBlock) => void;
};

export const HouseBlocksFormWithControls = ({ houseBlock, setHouseBlock }: Props) => {
    const [readOnly, setReadOnly] = useState(true);
    const [newHouseBlock, setNewHouseBlock] = useState<HouseBlock>(houseBlock);

    const handleSave = () => {
        setHouseBlock(newHouseBlock);
        setReadOnly(true);
    };

    const handleCancel = () => {
        setNewHouseBlock(houseBlock);
        setReadOnly(true);
    };

    return (
        <Box mt={4}>
            <Stack direction="row" alignItems="center" justifyContent="flex-end" spacing={2} mb={2}>
                {readOnly && (
                    <Tooltip placement="top" title={t("generic.edit")}>
                        <EditIcon sx={{ cursor: "pointer" }} onClick={() => setReadOnly(false)} />
                    </Tooltip>
                )}
                {!readOnly && (
                    <>
                        <Tooltip placement="top" title={t("generic.cancelChanges")}>
                            <ClearIcon sx={{ cursor: "pointer" }} onClick={handleCancel} />
                        </Tooltip>
                        <Tooltip placement="top" title={t("generic.saveChanges")}>
                            <SaveIcon sx={{ cursor: "pointer" }} onClick={handleSave} />
                        </Tooltip>
                    </>
                )}
            </Stack>
            <HouseBlocksForm houseBlock={newHouseBlock} setHouseBlock={setNewHouseBlock} readOnly={readOnly} />
        </Box>
    );
};
