import { Box, Button, Dialog, DialogActions, DialogTitle } from "@mui/material";
import { deleteProject } from "../../api/projectsServices";
import { useTranslation } from "react-i18next";
import AlertContext from "../../context/AlertContext";
import { useContext } from "react";

export default function DeleteProjectDialog({ setIsOpen, isOpen, projectName, projectId }: any) {
    const { t } = useTranslation();
    const { setAlert } = useContext(AlertContext);
    const onDelete = async (projectId: string) => {
        try {
            const res = await deleteProject(projectId);

            if (res.ok) {
                {
                    /*Alert should be updated and translated */
                }
                setAlert(t("projectDeletedSuccesMessage", { name: projectName }), "success");
            }
        } catch (error: any) {
            setAlert(error.message, "error");
        } finally {
            setIsOpen(false);
        }
    };

    return (
        <Dialog open={isOpen} onClose={() => setIsOpen(false)}>
            <DialogTitle>{t("generic.confirmDeletion", { name: projectName })}</DialogTitle>
            <DialogActions>
                <Box sx={{ display: "flex", gap: "10px" }}>
                    <Button onClick={() => setIsOpen(false)} variant="outlined">
                        {t("generic.no")}
                    </Button>
                    <Button onClick={() => onDelete(projectId)} variant="contained">
                        {t("generic.yes")}
                    </Button>
                </Box>
            </DialogActions>
        </Dialog>
    );
}
export {};
