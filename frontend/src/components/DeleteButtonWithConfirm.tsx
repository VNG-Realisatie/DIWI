import { useState } from "react";
import { t } from "i18next";
import { Box, Button, Dialog, DialogActions, DialogTitle, Tooltip } from "@mui/material";
import DeleteForeverOutlinedIcon from "@mui/icons-material/DeleteForeverOutlined";
import useAlert from "../hooks/useAlert";
import { Color } from "react-color";

type DeleteButtonWithConfirmProps = {
    typeAndName: string;
    iconColor: Color;
    deleteFunction: () => Promise<void>;
    afterDelete?: () => void;
};

export const DeleteButtonWithConfirm = ({ typeAndName: name, iconColor, deleteFunction, afterDelete }: DeleteButtonWithConfirmProps) => {
    const { setAlert } = useAlert();
    const [isDialogOpen, setIsDialogOpen] = useState<boolean>(false);

    const handleDelete = async () => {
        try {
            await deleteFunction();
            setAlert(t("generic.deletionSuccess", { name }), "success");
            if (afterDelete) afterDelete();
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
        } catch (error: any) {
            setAlert(error.message, "error");
        } finally {
            setIsDialogOpen(false);
        }
    };

    return (
        <>
            <Tooltip placement="top" title={t("generic.delete")}>
                <DeleteForeverOutlinedIcon
                    sx={{ ml: 2, color: iconColor.toString(), cursor: "pointer" }}
                    onClick={(event) => {
                        event.stopPropagation();
                        setIsDialogOpen(!isDialogOpen);
                    }}
                />
            </Tooltip>

            {isDialogOpen && (
                <Dialog open={isDialogOpen} onClose={() => setIsDialogOpen(false)}>
                    <DialogTitle>{t("generic.confirmDeletion", { name })}</DialogTitle>
                    <DialogActions>
                        <Box sx={{ display: "flex", gap: "10px" }}>
                            <Button
                                onClick={(event) => {
                                    event.stopPropagation();
                                    setIsDialogOpen(false);
                                }}
                                variant="outlined"
                            >
                                {t("generic.no")}
                            </Button>
                            <Button onClick={() => handleDelete()} variant="contained">
                                {t("generic.yes")}
                            </Button>
                        </Box>
                    </DialogActions>
                </Dialog>
            )}
        </>
    );
};
