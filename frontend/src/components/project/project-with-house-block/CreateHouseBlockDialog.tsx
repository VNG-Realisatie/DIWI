import { Dialog, DialogContent, DialogActions, Button } from "@mui/material";
import { t } from "i18next";
import { BlockHousesForm } from "../../BlockHousesForm";
import { HouseBlock } from "../../project-wizard/house-blocks/types";
import { addHouseBlock, getProjectHouseBlocks } from "../../../api/projectsServices";
import { useParams } from "react-router-dom";
import useAlert from "../../../hooks/useAlert";
import { emptyHouseBlockForm } from "../../project-wizard/house-blocks/constants";

type Props = {
    openHouseBlockDialog: boolean;
    setOpenHouseBlockDialog: (openDialog: boolean) => void;
    createFormHouseBlock: HouseBlock;
    setCreateFormHouseBlock: (hb: HouseBlock) => void;
    setHouseBlocks: (hb: HouseBlock[]) => void;
};
export const CreateHouseBlockDialog = ({
    openHouseBlockDialog,
    setOpenHouseBlockDialog,
    createFormHouseBlock,
    setCreateFormHouseBlock,
    setHouseBlocks,
}: Props) => {
    const { id } = useParams();
    const { setAlert } = useAlert();
    return (
        <Dialog open={openHouseBlockDialog} onClose={() => setOpenHouseBlockDialog(false)} maxWidth="xl">
            <DialogContent>
                <BlockHousesForm editForm={false} setCreateFormHouseBlock={setCreateFormHouseBlock} createFormHouseBlock={createFormHouseBlock} />
            </DialogContent>
            <DialogActions>
                <Button variant="contained" color="error" onClick={() => setOpenHouseBlockDialog(false)}>
                    {t("generic.cancel")}
                </Button>
                <Button
                    variant="contained"
                    color="success"
                    onClick={async () => {
                        const addedHouseBlock = await addHouseBlock({ ...createFormHouseBlock, projectId: id });
                        if (addedHouseBlock) {
                            setAlert(t("createProject.houseBlocksForm.notifications.successfullySaved"), "success");
                            setOpenHouseBlockDialog(false);
                            setCreateFormHouseBlock(emptyHouseBlockForm);
                            id && getProjectHouseBlocks(id).then((hb) => setHouseBlocks(hb));
                        } else {
                            setAlert(t("createProject.houseBlocksForm.notifications.error"), "error");
                        }
                    }}
                    autoFocus
                >
                    {t("generic.save")}
                </Button>
            </DialogActions>
        </Dialog>
    );
};
