import { Box, Stack, Tooltip } from "@mui/material";
import { HouseBlock } from "./project-wizard/house-blocks/types";
import { useContext, useEffect, useState } from "react";
import { emptyHouseBlockForm } from "./project-wizard/house-blocks/constants";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import ClearIcon from "@mui/icons-material/Clear";
import { t } from "i18next";
import { updateHouseBlock } from "../api/projectsServices";
import AlertContext from "../context/AlertContext";
import { CustomPropertyValue, getBlockCustomPropertyValues, putBlockCustomPropertyValues } from "../api/customPropServices";
import HouseBlockContext from "../context/HouseBlockContext";
import { HouseBlocksForm } from "./HouseBlocksForm";

type Props = {
    projectDetailHouseBlock?: HouseBlock;
    createFormHouseBlock: HouseBlock;
    setCreateFormHouseBlock: (hb: HouseBlock) => void;
    validationError?: string;
};

export const HouseBlocksFormWithControls = ({ projectDetailHouseBlock, createFormHouseBlock, setCreateFormHouseBlock }: Props) => {
    const [projectForm, setProjectForm] = useState<HouseBlock>(projectDetailHouseBlock ? projectDetailHouseBlock : emptyHouseBlockForm);
    const [readOnly, setReadOnly] = useState(true);
    const [customValues, setCustomValues] = useState<CustomPropertyValue[]>([]);

    const { setAlert } = useContext(AlertContext);
    const { houseBlocks, setHouseBlocks } = useContext(HouseBlockContext);

    useEffect(() => {
        const fetchCustomPropertyValues = async () => {
            try {
                const values = await getBlockCustomPropertyValues(projectForm.houseblockId as string);
                setCustomValues(values);
            } catch (error) {
                console.error("Error fetching custom property values:", error);
            }
        };

        fetchCustomPropertyValues();
    }, [projectForm.houseblockId]);

    const oldForm = projectDetailHouseBlock && { ...projectDetailHouseBlock };

    const handleCustomPropertiesSave = () => {
        customValues.forEach((value) => {
            putBlockCustomPropertyValues(projectForm.houseblockId as string, value).catch((error) => setAlert(error.message, "error"));
        });
    };
    const handleHouseBlockUpdate = () => {
        if (
            !projectForm.houseblockName ||
            !projectForm.startDate ||
            !projectForm.endDate ||
            projectForm.ownershipValue.some((owner) => owner.amount === null || isNaN(owner.amount))
        ) {
            setAlert(t("createProject.hasMissingRequiredAreas.hasmissingProperty"), "warning");
            return;
        }
        const updatedHouseBlock = houseBlocks.filter((hb) => hb.houseblockId !== projectForm.houseblockId);
        updateHouseBlock(projectForm)
            .then((res) => {
                setReadOnly(true);
                setAlert(t("generic.updated"), "success");
                setProjectForm(res);
                setHouseBlocks([...updatedHouseBlock, res]);
                handleCustomPropertiesSave();
            })
            .catch(() => setAlert(t("generic.failedToUpdate"), "error"));
    };
    return (
        <Box mt={4}>
            <Stack direction="row" alignItems="center" justifyContent="flex-end" spacing={2} mb={2}>
                {readOnly && (
                    <Tooltip placement="top" title={t("generic.edit")}>
                        <EditIcon sx={{ cursor: "pointer" }} onClick={() => setReadOnly(false)} />
                    </Tooltip>
                )}
                {!readOnly && (
                    <>
                        <Tooltip placement="top" title={t("generic.cancelChanges")}>
                            <ClearIcon
                                sx={{ cursor: "pointer" }}
                                onClick={() => {
                                    setReadOnly(true);
                                    oldForm && setProjectForm(oldForm);
                                }}
                            />
                        </Tooltip>
                        <Tooltip placement="top" title={t("generic.saveChanges")}>
                            <SaveIcon sx={{ cursor: "pointer" }} onClick={handleHouseBlockUpdate} />
                        </Tooltip>
                    </>
                )}
            </Stack>
            <HouseBlocksForm
                projectDetailHouseBlock={projectDetailHouseBlock}
                readOnly={readOnly}
                createFormHouseBlock={createFormHouseBlock}
                setCreateFormHouseBlock={setCreateFormHouseBlock}
            />
        </Box>
    );
};
