import { Grid, Box, Accordion, AccordionSummary, AccordionDetails } from "@mui/material";
import { BlockHousesForm } from "../../BlockHousesForm";
import { HouseBlock } from "../../project-wizard/house-blocks/types";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";

type Props = {
    houseBlocks?: HouseBlock[];
    setOpenHouseBlockDialog: (open: boolean) => void;
};
export const HouseBlocksList = ({ setOpenHouseBlockDialog, houseBlocks }: Props) => {
    return (
        <Grid container my={2}>
            <Box sx={{ cursor: "pointer", ml: "auto" }} onClick={() => setOpenHouseBlockDialog(true)}>
                <AddCircleIcon color="info" sx={{ fontSize: "40px" }} />
            </Box>
            {houseBlocks?.map((hb: HouseBlock, i: number) => {
                return (
                    <Accordion sx={{ width: "100%" }}>
                        <AccordionSummary
                            sx={{ backgroundColor: "#00A9F3", color: "#ffffff" }}
                            expandIcon={<ExpandMoreIcon sx={{ color: "#ffffff" }} />}
                            aria-controls="panel1-content"
                            id="panel1-header"
                        >
                            {hb.houseblockName}
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
