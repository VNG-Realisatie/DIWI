import { Dialog, DialogContent, DialogActions, Button } from "@mui/material";
import { t } from "i18next";
import { BlockHousesForm } from "../../BlockHousesForm";
import { HouseBlock } from "../../project-wizard/house-blocks/types";

type Props = {
    openHouseBlockDialog: boolean;
    setOpenHouseBlockDialog: (openDialog: boolean) => void;
    createFormHouseBlock: HouseBlock;
    setCreateFormHouseBlock: (hb: HouseBlock) => void;
};
export const CreateHouseBlockDialog = ({ openHouseBlockDialog, setOpenHouseBlockDialog, createFormHouseBlock, setCreateFormHouseBlock }: Props) => {
    return (
        <Dialog open={openHouseBlockDialog} onClose={() => setOpenHouseBlockDialog(false)} maxWidth="xl">
            <DialogContent>
                <BlockHousesForm editForm={false} setCreateFormHouseBlock={setCreateFormHouseBlock} createFormHouseBlock={createFormHouseBlock} />
            </DialogContent>
            <DialogActions>
                <Button onClick={() => setOpenHouseBlockDialog(false)}>{t("generic.cancel")}</Button>
                <Button onClick={() => console.log("add endpoint will be here")} autoFocus>
                    {t("generic.save")}
                </Button>
            </DialogActions>
        </Dialog>
    );
};
