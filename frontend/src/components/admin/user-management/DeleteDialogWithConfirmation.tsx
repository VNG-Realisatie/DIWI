import { Dialog, DialogActions, DialogContent, DialogContentText, Button } from "@mui/material";
import { useTranslation } from "react-i18next";

type DeleteDialogProps = {
    open: boolean;
    onClose: () => void;
    onConfirm: () => void;
    dialogContentText: string;
};

const DeleteDialogWithConfirmation = ({ open, onClose, onConfirm, dialogContentText }: DeleteDialogProps) => {
    const { t } = useTranslation();

    return (
        <Dialog open={open} onClose={onClose}>
            <DialogContent>
                <DialogContentText>{t(dialogContentText)}</DialogContentText>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose} variant="outlined">
                    {t("generic.no")}
                </Button>
                <Button onClick={onConfirm} variant="contained">
                    {t("generic.yes")}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default DeleteDialogWithConfirmation;
