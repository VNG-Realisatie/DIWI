import { Button, Dialog, DialogActions, DialogContent, Typography } from "@mui/material";
import { t } from "i18next";

type Props = {
    open: boolean;
    handleDelete: () => void;
    closeDeleteDialog: () => void;
};

const DeleteDialog = ({ open, handleDelete, closeDeleteDialog }: Props) => {
    return (
        <Dialog open={open} onClose={closeDeleteDialog}>
            <DialogContent>
                <Typography>{t("admin.priceCategories.deleteConfirmation")}</Typography>
            </DialogContent>
            <DialogActions>
                <Button onClick={closeDeleteDialog} variant="outlined">
                    {t("generic.no")}
                </Button>
                <Button onClick={handleDelete} variant="contained" autoFocus>
                    {t("generic.yes")}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default DeleteDialog;
