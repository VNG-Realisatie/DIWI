import { Dialog, DialogContent, DialogActions, Button } from "@mui/material";
import { t } from "i18next";
import { HouseBlocksForm } from "../../HouseBlocksForm";
import { HouseBlock } from "../../project-wizard/house-blocks/types";
import { useContext, useState } from "react";
import HouseBlockContext from "../../../context/HouseBlockContext";

type Props = {
    openHouseBlockDialog: boolean;
    setOpenHouseBlockDialog: (openDialog: boolean) => void;
};
export const CreateHouseBlockDialog = ({ openHouseBlockDialog, setOpenHouseBlockDialog }: Props) => {
    const { addHouseBlock, getEmptyHouseBlock } = useContext(HouseBlockContext);
    const [houseBlock, setHouseBlock] = useState<HouseBlock>(getEmptyHouseBlock());

    return (
        <Dialog open={openHouseBlockDialog} onClose={() => setOpenHouseBlockDialog(false)} maxWidth="xl">
            <DialogContent>
                <HouseBlocksForm houseBlock={houseBlock} setHouseBlock={setHouseBlock} readOnly={false} />
            </DialogContent>
            <DialogActions>
                <Button variant="contained" color="error" onClick={() => setOpenHouseBlockDialog(false)}>
                    {t("generic.cancel")}
                </Button>
                <Button
                    variant="contained"
                    color="success"
                    onClick={() => {
                        addHouseBlock(houseBlock);
                        setOpenHouseBlockDialog(false);
                    }}
                    autoFocus
                >
                    {t("generic.save")}
                </Button>
            </DialogActions>
        </Dialog>
    );
};
