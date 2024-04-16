import { Grid, Accordion, AccordionSummary, AccordionDetails } from "@mui/material";
import { HouseBlockWithCustomProperties } from "../../../types/houseBlockTypes";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { AddHouseBlockButton } from "../../PlusButton";
import { useTranslation } from "react-i18next";
import { useContext } from "react";
import HouseBlockContext from "../../../context/HouseBlockContext";
import { HouseBlocksFormWithControls } from "../../HouseBlocksFormWithControls";
import { sortHouseBlockByNameAndId as sortHouseBlocksByNameAndId } from "../../../utils/sortFunctions";

type Props = {
    setOpenHouseBlockDialog: (open: boolean) => void;
};
export const HouseBlocksList = ({ setOpenHouseBlockDialog }: Props) => {
    const { houseBlocks } = useContext(HouseBlockContext);
    const { t } = useTranslation();

    return (
        <Grid container my={2}>
            <AddHouseBlockButton onClick={() => setOpenHouseBlockDialog(true)} />
            {houseBlocks.sort(sortHouseBlocksByNameAndId).map((hb: HouseBlockWithCustomProperties) => {
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
                            <HouseBlocksFormWithControls houseBlock={hb} />
                        </AccordionDetails>
                    </Accordion>
                );
            })}
        </Grid>
    );
};
