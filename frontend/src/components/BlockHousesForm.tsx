import { Box, Grid, Stack, Tooltip } from "@mui/material";
import { HouseBlock } from "./project-wizard/house-blocks/types";
import { GeneralInformationGroup } from "./project-wizard/house-blocks/general-information/GeneralInformationGroup";
import { MutationInformationGroup } from "./project-wizard/house-blocks/mutation-information/MutationInformationGroup";
import { useContext, useEffect, useState } from "react";
import { OwnershipInformationGroup } from "./project-wizard/house-blocks/ownership-information/OwnershipInformationGroup";
import { PhysicalAppeareanceGroup } from "./project-wizard/house-blocks/physical-appearence/PhysicalAppeareanceGroup";
import { PurposeGroup } from "./project-wizard/house-blocks/purpose/PurposeGroup";
import { HouseTypeGroup } from "./project-wizard/house-blocks/house-type/HouseTypeGroup";
import { GroundPositionGroup } from "./project-wizard/house-blocks/ground-position/GroundPositionGroup";
import { Programming } from "./project-wizard/house-blocks/programming/Programming";
import { emptyHouseBlockForm } from "./project-wizard/house-blocks/constants";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import ClearIcon from "@mui/icons-material/Clear";
import { t } from "i18next";
import { CustomPropertyType, getCustomPropertiesWithQuery } from "../api/adminSettingServices";
import { columnTitleStyle } from "./project/project-with-house-block/ProjectWithHouseBlock";
import { updateHouseBlock } from "../api/projectsServices";
import AlertContext from "../context/AlertContext";

type Props = {
    projectDetailHouseBlock?: HouseBlock;
    editForm: boolean;
    createFormHouseBlock: HouseBlock;
    setCreateFormHouseBlock: (hb: HouseBlock) => void;
};

export const BlockHousesForm = ({ projectDetailHouseBlock, editForm, createFormHouseBlock, setCreateFormHouseBlock }: Props) => {
    const [projectForm, setProjectForm] = useState<HouseBlock>(projectDetailHouseBlock ? projectDetailHouseBlock : emptyHouseBlockForm);
    const [edit, setEdit] = useState(false);

    const { setAlert } = useContext(AlertContext);

    const oldForm = projectDetailHouseBlock && { ...projectDetailHouseBlock };
    const defineProjectState = () => {
        if (editForm) {
            return projectForm;
        } else {
            return createFormHouseBlock;
        }
    };

    const defineProjectUpdateState = () => {
        if (editForm) {
            return setProjectForm;
        } else {
            return setCreateFormHouseBlock;
        }
    };

    useEffect(() => {
        getCustomPropertiesWithQuery("WONINGBLOK").then((customProperties) => setCustomProperties(customProperties));
    }, []);

    const handleHouseBlockUpdate = () => {
        updateHouseBlock(projectForm)
            .then((res) => {
                setEdit(false);
                setAlert(t("generic.updated"), "success");
                setProjectForm(res);
            })
            .catch(() => setAlert(t("generic.failedToUpdate"), "error"));
    };
    return (
        <Box mt={4}>
            {editForm && (
                <Stack direction="row" alignItems="center" justifyContent="flex-end" spacing={2} mb={2}>
                    {!edit && (
                        <Tooltip placement="top" title={t("generic.edit")}>
                            <EditIcon sx={{ cursor: "pointer" }} onClick={() => setEdit(true)} />
                        </Tooltip>
                    )}
                    {edit && (
                        <>
                            <Tooltip placement="top" title={t("generic.cancelChanges")}>
                                <ClearIcon
                                    sx={{ cursor: "pointer" }}
                                    onClick={() => {
                                        setEdit(false);
                                        oldForm && setProjectForm(oldForm);
                                    }}
                                />
                            </Tooltip>
                            <Tooltip placement="top" title={t("generic.saveChanges")}>
                                {/* TODO integrate later updatehouseblock endpoint */}
                                <SaveIcon sx={{ cursor: "pointer" }} onClick={handleHouseBlockUpdate} />
                            </Tooltip>
                        </>
                    )}
                </Stack>
            )}
            <Grid container spacing={2} alignItems="stretch">
                <Grid item xs={12} md={8}>
                    <GeneralInformationGroup projectForm={defineProjectState()} setProjectForm={defineProjectUpdateState()} edit={edit} editForm={editForm} />
                </Grid>
                <Grid item xs={12} md={4}>
                    <MutationInformationGroup projectForm={defineProjectState()} setProjectForm={defineProjectUpdateState()} edit={edit} editForm={editForm} />
                </Grid>
            </Grid>
            <Grid container mt={2}>
                <Grid item xs={12}>
                    <OwnershipInformationGroup projectForm={defineProjectState()} setProjectForm={defineProjectUpdateState()} edit={edit} editForm={editForm} />
                </Grid>
            </Grid>
            <Grid container spacing={2} alignItems="stretch" mt={0.5}>
                <Grid item xs={12} md={4}>
                    <PhysicalAppeareanceGroup projectForm={defineProjectState()} setProjectForm={defineProjectUpdateState()} edit={edit} editForm={editForm} />
                </Grid>
                <Grid item xs={12} md={4}>
                    <PurposeGroup projectForm={defineProjectState()} setProjectForm={defineProjectUpdateState()} edit={edit} editForm={editForm} />
                </Grid>
                <Grid item xs={12} md={4}>
                    <HouseTypeGroup projectForm={defineProjectState()} setProjectForm={defineProjectUpdateState()} edit={edit} editForm={editForm} />
                </Grid>
            </Grid>
            <Grid container spacing={2} alignItems="stretch" mt={0.5}>
                <Grid item xs={12} md={8}>
                    <GroundPositionGroup projectForm={defineProjectState()} setProjectForm={defineProjectUpdateState()} edit={edit} editForm={editForm} />
                </Grid>
                <Grid item xs={12} md={4}>
                    <Programming projectForm={defineProjectState()} setProjectForm={defineProjectUpdateState()} edit={edit} editForm={editForm} />
                </Grid>
            </Grid>
            <Grid container spacing={2} alignItems="stretch" mt={0.5}>
                <Grid item xs={12}>
                    <CustomPropertiesGroup />
                </Grid>
            </Grid>
        </Box>
    );
};
