import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from "@mui/material";
import { useState } from "react";

type Props = {
    open: boolean;
};

const DeleteDialog = ({ open }: Props) => {
    const handleClose = () => {};

    return (
        <Dialog open={open} onClose={handleClose}>
            <DialogContent>
                <p>Are you sure you want to delete?</p>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleClose} color="primary">
                    Cancel
                </Button>
                <Button onClick={handleClose} color="primary" autoFocus>
                    Delete
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default DeleteDialog;
