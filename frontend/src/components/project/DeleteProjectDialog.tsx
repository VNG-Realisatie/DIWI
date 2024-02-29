import { Box, Button, Dialog, DialogActions, DialogTitle } from "@mui/material";
import { deleteProject } from "../../api/projectsServices";
import { useTranslation } from "react-i18next";

export default function DeleteProjectDialog({ setIsOpen, isOpen, projectName, projectId }: any) {
    const { t } = useTranslation();
    return (
        <Dialog open={isOpen} onClose={() => setIsOpen(false)}>
            <DialogTitle>Are you sure you want to delete {projectName}?</DialogTitle>
            {/* also needs to be translated */}
            <DialogActions>
                <Box sx={{ display: "flex", gap: "10px" }}>
                    <Button onClick={() => setIsOpen(false)} variant="outlined">
                        {t("generic.no")}
                    </Button>
                    <Button onClick={() => deleteProject(projectId)} variant="contained">
                        {t("generic.yes")}
                    </Button>
                </Box>
            </DialogActions>
        </Dialog>
    );
}
export {};
