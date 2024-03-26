import { Grid, Accordion, AccordionSummary, AccordionDetails } from "@mui/material";
import { BlockHousesForm } from "../../BlockHousesForm";
import { HouseBlock } from "../../project-wizard/house-blocks/types";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { AddHouseBlockButton } from "../../PlusButton";
import { useTranslation } from "react-i18next";

type Props = {
    houseBlocks?: HouseBlock[];
    setOpenHouseBlockDialog: (open: boolean) => void;
};
export const HouseBlocksList = ({ setOpenHouseBlockDialog, houseBlocks }: Props) => {
    const { t } = useTranslation();
    return (
        <Grid container my={2}>
            <AddHouseBlockButton onClick={() => setOpenHouseBlockDialog(true)} />
            {houseBlocks?.map((hb: HouseBlock, i: number) => {
                return (
                    <Accordion sx={{ width: "100%" }} key={i}>
                        <AccordionSummary
                            sx={{ backgroundColor: "#00A9F3", color: "#ffffff" }}
                            expandIcon={<ExpandMoreIcon sx={{ color: "#ffffff" }} />}
                            aria-controls="panel1-content"
                            id="panel1-header"
                        >
                            {hb.houseblockName}: {hb.mutation.grossPlanCapacity} {t("createProject.houseBlocksForm.housesOn")} {hb.endDate}
                        </AccordionSummary>
                        <AccordionDetails>
                            <BlockHousesForm
                                projectDetailHouseBlock={hb}
                                key={i}
                                editForm={true}
                                createFormHouseBlock={hb}
                                setCreateFormHouseBlock={(hb) => {}}
                            />
                        </AccordionDetails>
                    </Accordion>
                );
            })}
        </Grid>
    );
};
