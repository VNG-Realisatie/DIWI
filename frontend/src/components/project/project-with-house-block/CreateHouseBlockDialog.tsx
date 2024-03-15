import { Dialog, DialogContent, DialogActions, Button } from "@mui/material";
import { t } from "i18next";
import { BlockHousesForm } from "../../BlockHousesForm";

type Props = {
    openHouseBlockDialog: boolean;
    setOpenHouseBlockDialog: (openDialog: boolean) => void;
};
export const CreateHouseBlockDialog = ({ openHouseBlockDialog, setOpenHouseBlockDialog }: Props) => {
    return (
        <Dialog open={openHouseBlockDialog} onClose={() => setOpenHouseBlockDialog(false)} maxWidth="xl">
            <DialogContent>
                <BlockHousesForm editForm={false} />
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
