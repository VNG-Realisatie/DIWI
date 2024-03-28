import { Dialog, DialogContent, DialogActions, Button } from "@mui/material";
import { t } from "i18next";
import { HouseBlocksForm } from "../../HouseBlocksForm";
import { HouseBlock } from "../../project-wizard/house-blocks/types";
import { addHouseBlock, getProjectHouseBlocks } from "../../../api/projectsServices";
import { useParams } from "react-router-dom";
import useAlert from "../../../hooks/useAlert";
import { emptyHouseBlockForm } from "../../project-wizard/house-blocks/constants";
import ProjectContext from "../../../context/ProjectContext";
import { useContext } from "react";
import HouseBlockContext from "../../../context/HouseBlockContext";

type Props = {
    openHouseBlockDialog: boolean;
    setOpenHouseBlockDialog: (openDialog: boolean) => void;
    createFormHouseBlock: HouseBlock;
    setCreateFormHouseBlock: (hb: HouseBlock) => void;
};
export const CreateHouseBlockDialog = ({ openHouseBlockDialog, setOpenHouseBlockDialog, createFormHouseBlock, setCreateFormHouseBlock }: Props) => {
    const { id } = useParams();
    const { setAlert } = useAlert();
    const { updateProject } = useContext(ProjectContext);
    const { setHouseBlocks } = useContext(HouseBlockContext);
    const handleSave = async () => {
        if (
            !createFormHouseBlock.houseblockName ||
            !createFormHouseBlock.startDate ||
            !createFormHouseBlock.endDate ||
            createFormHouseBlock.ownershipValue.some((owner) => owner.amount === null || isNaN(owner.amount))
        ) {
            setAlert(t("createProject.hasMissingRequiredAreas.hasmissingProperty"), "warning");
            return;
        }
        try {
            const addedHouseBlock = await addHouseBlock({ ...createFormHouseBlock, projectId: id });
            if (addedHouseBlock) {
                setAlert(t("createProject.houseBlocksForm.notifications.successfullySaved"), "success");
                setOpenHouseBlockDialog(false);
                setCreateFormHouseBlock(emptyHouseBlockForm);
                if (id) {
                    const hb = await getProjectHouseBlocks(id);
                    setHouseBlocks(hb);
                    updateProject();
                }
            } else {
                setAlert(t("createProject.houseBlocksForm.notifications.error"), "error");
            }
        } catch (error: any) {
            setAlert(error.message, "error");
        }
    };
    return (
        <Dialog open={openHouseBlockDialog} onClose={() => setOpenHouseBlockDialog(false)} maxWidth="xl">
            <DialogContent>
                <HouseBlocksForm readOnly={false} setCreateFormHouseBlock={setCreateFormHouseBlock} createFormHouseBlock={createFormHouseBlock} />
            </DialogContent>
            <DialogActions>
                <Button variant="contained" color="error" onClick={() => setOpenHouseBlockDialog(false)}>
                    {t("generic.cancel")}
                </Button>
                <Button variant="contained" color="success" onClick={handleSave} autoFocus>
                    {t("generic.save")}
                </Button>
            </DialogActions>
        </Dialog>
    );
};
