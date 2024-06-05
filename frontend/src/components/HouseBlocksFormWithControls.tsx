import { AlertColor, Box, Stack, Tooltip } from "@mui/material";
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
import { isOwnershipAmountValid } from "./project-wizard/house-blocks/ownership-information/OwnershipRowInputs";

type Props = {
    houseBlock: HouseBlockWithCustomProperties;
};

// eslint-disable-next-line react-refresh/only-export-components
export const validateHouseBlock = (houseBlock: HouseBlockWithCustomProperties, setAlert: (message: string, type: AlertColor) => void): boolean => {
    let isValid = true;
    const invalidOwnershipAmount = houseBlock.ownershipValue.some((owner) => !isOwnershipAmountValid(owner.amount));
    if (
        !houseBlock.endDate ||
        !houseBlock.startDate ||
        !houseBlock.houseblockName ||
        !houseBlock.mutation.amount ||
        houseBlock.mutation.amount <= 0 ||
        !houseBlock.mutation.kind ||
        invalidOwnershipAmount
    ) {
        isValid = false;
    }
    if (!isValid) {
        setAlert(t("createProject.houseBlocksForm.notifications.error"), "warning");
    }
    return isValid;
};

export const HouseBlocksFormWithControls = ({ houseBlock }: Props) => {
    const [readOnly, setReadOnly] = useState(true);
    const [newHouseBlock, setNewHouseBlock] = useState<HouseBlockWithCustomProperties>(houseBlock);
    const { refresh } = useContext(HouseBlockContext);
    const { setAlert } = useAlert();

    const handleSave = () => {
        if (validateHouseBlock(newHouseBlock, setAlert)) {
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
            </Stack>
            <HouseBlocksForm houseBlock={newHouseBlock} setHouseBlock={setNewHouseBlock} readOnly={readOnly} />
        </Box>
    );
};
