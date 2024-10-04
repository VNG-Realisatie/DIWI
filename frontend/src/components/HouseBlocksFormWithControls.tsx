import { Box, Stack, Tooltip } from "@mui/material";
import { HouseBlockWithCustomProperties } from "../types/houseBlockTypes";
import { useContext, useState } from "react";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import ClearIcon from "@mui/icons-material/Clear";
import { t } from "i18next";
import { HouseBlocksForm } from "./HouseBlocksForm";
import { DeleteButtonWithConfirm } from "./DeleteButtonWithConfirm";
import { deleteHouseBlockWithCustomProperties, saveHouseBlockWithCustomProperties } from "../api/houseBlockServices";
import HouseBlockContext from "../context/HouseBlockContext";
import useAlert from "../hooks/useAlert";
import useAllowedActions from "../hooks/useAllowedActions";
import { validateHouseBlock } from "../utils/houseblocks/houseBlocksFunctions";

type Props = {
    houseBlock: HouseBlockWithCustomProperties;
};

export const HouseBlocksFormWithControls = ({ houseBlock }: Props) => {
    const [readOnly, setReadOnly] = useState(true);
    const [newHouseBlock, setNewHouseBlock] = useState<HouseBlockWithCustomProperties>(houseBlock);
    const { refresh, customDefinitions } = useContext(HouseBlockContext);
    const { setAlert } = useAlert();
    const { allowedActions } = useAllowedActions();

    const handleSave = () => {
        if (validateHouseBlock(newHouseBlock, setAlert, customDefinitions)) {
            saveHouseBlockWithCustomProperties(newHouseBlock);
            setReadOnly(true);
        }
    };

    const handleCancel = () => {
        setNewHouseBlock(houseBlock);
        setReadOnly(true);
    };

    return (
        <Box mt={4}>
            <Stack direction="row" alignItems="center" justifyContent="flex-end" spacing={2} mb={2}>
                {allowedActions.includes("EDIT_OWN_PROJECTS") && (
                    <>
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

                        {houseBlock.houseblockId && (
                            <DeleteButtonWithConfirm
                                typeAndName={`${t("generic.houseblock")} ${houseBlock.houseblockName}`}
                                iconColor={"red"}
                                deleteFunction={async () => {
                                    if (houseBlock?.houseblockId) {
                                        await deleteHouseBlockWithCustomProperties(houseBlock.houseblockId);
                                        refresh();
                                    }
                                }}
                            />
                        )}
                    </>
                )}
            </Stack>
            <HouseBlocksForm houseBlock={newHouseBlock} setHouseBlock={setNewHouseBlock} readOnly={readOnly} />
        </Box>
    );
};
