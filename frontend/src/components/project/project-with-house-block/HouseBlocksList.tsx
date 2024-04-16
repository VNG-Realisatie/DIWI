import { Grid, Accordion, AccordionSummary, AccordionDetails } from "@mui/material";
import { HouseBlock } from "../../project-wizard/house-blocks/types";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { AddHouseBlockButton } from "../../PlusButton";
import { useTranslation } from "react-i18next";
import { useContext } from "react";
import HouseBlockContext from "../../../context/HouseBlockContext";
import { HouseBlocksFormWithControls } from "../../HouseBlocksFormWithControls";

type Props = {
    setOpenHouseBlockDialog: (open: boolean) => void;
};
export const HouseBlocksList = ({ setOpenHouseBlockDialog }: Props) => {
    const { houseBlocks, updateHouseBlock } = useContext(HouseBlockContext);
    const { t } = useTranslation();

    const handleUpdateHouseBlock = (houseBlock: HouseBlock) => {
        updateHouseBlock(houseBlock);
    };

    const sortByNameAndId = (a: HouseBlock, b: HouseBlock) => {
        const firstSmaller = 1;
        // first sort by name
        if (a.houseblockName < b.houseblockName) return firstSmaller;
        if (a.houseblockName > b.houseblockName) return -firstSmaller;
        // if names identical, sort by id which cannot be identical
        if (a.houseblockId && b.houseblockId && a.houseblockId < b.houseblockId) return firstSmaller;
        return -firstSmaller;
    };

    return (
        <Grid container my={2}>
            <AddHouseBlockButton onClick={() => setOpenHouseBlockDialog(true)} />
            {houseBlocks.sort(sortByNameAndId).map((hb: HouseBlock) => {
                return (
                    <Accordion sx={{ width: "100%" }} key={hb.houseblockId}>
                        <AccordionSummary
                            sx={{ backgroundColor: "#00A9F3", color: "#ffffff" }}
                            expandIcon={<ExpandMoreIcon sx={{ color: "#ffffff" }} />}
                            aria-controls="panel1-content"
                            id="panel1-header"
                        >
                            {hb.houseblockName}: {hb.mutation.grossPlanCapacity} {t("createProject.houseBlocksForm.housesOn")} {hb.endDate}
                        </AccordionSummary>
                        <AccordionDetails>
                            <HouseBlocksFormWithControls houseBlock={hb} setHouseBlock={handleUpdateHouseBlock} />
                        </AccordionDetails>
                    </Accordion>
                );
            })}
        </Grid>
    );
};
