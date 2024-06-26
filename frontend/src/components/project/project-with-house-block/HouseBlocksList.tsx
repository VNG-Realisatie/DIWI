import { Grid, Accordion, AccordionSummary, AccordionDetails, Stack, Box, Tooltip } from "@mui/material";
import { HouseBlockWithCustomProperties } from "../../../types/houseBlockTypes";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { AddHouseBlockButton } from "../../PlusButton";
import { useTranslation } from "react-i18next";
import { useContext, useState } from "react";
import HouseBlockContext from "../../../context/HouseBlockContext";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import ClearIcon from "@mui/icons-material/Clear";
import { sortHouseBlockByNameAndId as sortHouseBlocksByNameAndId } from "../../../utils/sortFunctions";
import { deleteHouseBlockWithCustomProperties, saveHouseBlockWithCustomProperties } from "../../../api/houseBlockServices";
import { DeleteButtonWithConfirm } from "../../DeleteButtonWithConfirm";
import { HouseBlocksForm } from "../../HouseBlocksForm";
import { validateHouseBlock } from "../../HouseBlocksFormWithControls";
import useAlert from "../../../hooks/useAlert";
import { useCustomPropertyDefinitions } from "../../../hooks/useCustomPropertyDefinitions";
import useAllowedActions from "../../../hooks/useAllowedActions";

type Props = {
    setOpenHouseBlockDialog: (open: boolean) => void;
};
export const HouseBlocksList = ({ setOpenHouseBlockDialog }: Props) => {
    const { houseBlocks, refresh } = useContext(HouseBlockContext);

    return (
        <>
            <Grid container my={2}>
                {houseBlocks.sort(sortHouseBlocksByNameAndId).map((hb: HouseBlockWithCustomProperties) => (
                    <HouseBlockAccordionWithControls key={hb.houseblockId} houseBlock={hb} refresh={refresh} />
                ))}
            </Grid>
            <Box style={{ position: "fixed", bottom: 35, right: 40 }}>
                <AddHouseBlockButton onClick={() => setOpenHouseBlockDialog(true)} />
            </Box>
        </>
    );
};

type HouseBlockAccordionProps = {
    houseBlock: HouseBlockWithCustomProperties;
    refresh: () => void;
};

export const HouseBlockAccordionWithControls = ({ houseBlock, refresh }: HouseBlockAccordionProps) => {
    const [newHouseBlock, setNewHouseBlock] = useState<HouseBlockWithCustomProperties>(houseBlock);
    const [readOnly, setReadOnly] = useState(true);
    const [expanded, setExpanded] = useState(false);
    const { t } = useTranslation();
    const { setAlert } = useAlert();
    const { targetGroupCategories, physicalAppearanceCategories } = useCustomPropertyDefinitions();
    const allowedActions = useAllowedActions();

    const handleSave = async () => {
        if (validateHouseBlock(newHouseBlock, setAlert)) {
            try {
                const targetGroupCategoryIds = targetGroupCategories?.map((cat) => cat.id);
                const physicalAppearanceCategoryIds = physicalAppearanceCategories?.map((cat) => cat.id);

                const filteredHouseBlock = {
                    ...newHouseBlock,
                    targetGroup: newHouseBlock.targetGroup.filter((tg) => targetGroupCategoryIds?.includes(tg.id)),
                    physicalAppearance: newHouseBlock.physicalAppearance.filter((pa) => physicalAppearanceCategoryIds?.includes(pa.id)),
                };

                await saveHouseBlockWithCustomProperties(filteredHouseBlock);
                refresh();
                setReadOnly(true);
            } catch (error: unknown) {
                if (error instanceof Error) setAlert(error.message, "warning");
            }
        }
    };
    const handleCancel = () => {
        setNewHouseBlock(houseBlock);
        setReadOnly(true);
    };

    const handleDelete = async () => {
        if (houseBlock?.houseblockId) {
            await deleteHouseBlockWithCustomProperties(houseBlock.houseblockId);
            refresh();
        }
    };

    return (
        <Accordion
            sx={{ width: "100%" }}
            expanded={expanded}
            onChange={(event, isExpanded) => {
                setExpanded(isExpanded);
            }}
            disableGutters
        >
            <AccordionSummary
                sx={{ backgroundColor: "#00A9F3", color: "#ffffff" }}
                expandIcon={<ExpandMoreIcon sx={{ color: "#ffffff" }} />}
                aria-controls="panel1-content"
                id="panel1-header"
            >
                {houseBlock.houseblockName}: {houseBlock.mutation.amount} {t("createProject.houseBlocksForm.housesOn")} {houseBlock.endDate}
                <Stack>
                    {allowedActions.includes("EDIT_OWN_PROJECTS") && (
                        <>
                            <Box sx={{ cursor: "pointer" }} position="absolute" right={60} top={13}>
                                {readOnly ? (
                                    <Tooltip placement="top" title={t("generic.edit")}>
                                        <EditIcon
                                            sx={{ cursor: "pointer" }}
                                            data-testid="edit-houseblock"
                                            onClick={(event) => {
                                                event.stopPropagation();
                                                if (!expanded) setExpanded(true);
                                                setReadOnly(false);
                                            }}
                                        />
                                    </Tooltip>
                                ) : (
                                    <>
                                        <Tooltip placement="top" title={t("generic.cancelChanges")}>
                                            <ClearIcon
                                                sx={{ mr: 2, cursor: "pointer" }}
                                                onClick={(event) => {
                                                    event.stopPropagation();
                                                    handleCancel();
                                                }}
                                            />
                                        </Tooltip>
                                        <Tooltip placement="top" title={t("generic.saveChanges")}>
                                            <SaveIcon
                                                sx={{ cursor: "pointer" }}
                                                data-testid="save-houseblock"
                                                onClick={(event) => {
                                                    event.stopPropagation();
                                                    handleSave();
                                                }}
                                            />
                                        </Tooltip>
                                    </>
                                )}
                                {houseBlock.houseblockId && (
                                    <DeleteButtonWithConfirm
                                        typeAndName={`${t("generic.houseblock")} ${houseBlock.houseblockName}`}
                                        iconColor={"white"}
                                        deleteFunction={handleDelete}
                                    />
                                )}
                            </Box>
                        </>
                    )}
                </Stack>
            </AccordionSummary>
            <AccordionDetails>
                <Box mt={2}>
                    <HouseBlocksForm houseBlock={newHouseBlock} readOnly={readOnly} setHouseBlock={setNewHouseBlock} />
                </Box>
            </AccordionDetails>
        </Accordion>
    );
};
