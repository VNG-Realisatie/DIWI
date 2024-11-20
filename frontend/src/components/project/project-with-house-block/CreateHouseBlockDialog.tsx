import { Dialog, DialogContent, DialogActions, Button } from "@mui/material";
import { t } from "i18next";
import { HouseBlocksForm } from "../../HouseBlocksForm";
import { HouseBlockWithCustomProperties } from "../../../types/houseBlockTypes";
import { useContext, useEffect, useState } from "react";
import HouseBlockContext from "../../../context/HouseBlockContext";
import { saveHouseBlockWithCustomProperties } from "../../../api/houseBlockServices";
import useAlert from "../../../hooks/useAlert";
import { validateHouseBlock } from "../../../utils/houseblocks/houseBlocksFunctions";
import ProjectContext from "../../../context/ProjectContext";

type Props = {
    openHouseBlockDialog: boolean;
    setOpenHouseBlockDialog: (openDialog: boolean) => void;
};
export const CreateHouseBlockDialog = ({ openHouseBlockDialog, setOpenHouseBlockDialog }: Props) => {
    const { refresh, getEmptyHouseBlock, nonFixedCustomDefinitions } = useContext(HouseBlockContext);
    const { updateProject } = useContext(ProjectContext);
    const [houseBlock, setHouseBlock] = useState<HouseBlockWithCustomProperties>(getEmptyHouseBlock());
    const { setAlert } = useAlert();

    useEffect(() => {
        if (!openHouseBlockDialog) {
            setHouseBlock(getEmptyHouseBlock());
        }
    }, [openHouseBlockDialog, getEmptyHouseBlock, setHouseBlock]);

    return (
        <Dialog open={openHouseBlockDialog} onClose={() => setOpenHouseBlockDialog(false)} maxWidth="xl">
            <DialogContent>
                <HouseBlocksForm houseBlock={houseBlock} setHouseBlock={setHouseBlock} readOnly={false} />
            </DialogContent>
            <DialogActions>
                <Button
                    variant="contained"
                    color="error"
                    onClick={() => {
                        setOpenHouseBlockDialog(false);
                    }}
                >
                    {t("generic.cancel")}
                </Button>
                <Button
                    variant="contained"
                    color="success"
                    onClick={async () => {
                        if (validateHouseBlock(houseBlock, setAlert, nonFixedCustomDefinitions)) {
                            try {
                                await saveHouseBlockWithCustomProperties(houseBlock);
                                refresh();
                                updateProject();
                                setOpenHouseBlockDialog(false);
                                setAlert(t("generic.saved"), "success");
                            } catch (error: unknown) {
                                if (error instanceof Error) setAlert(error.message, "warning");
                            }
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
