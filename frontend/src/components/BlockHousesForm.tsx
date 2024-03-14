import { Box, Grid, Stack, Tooltip } from "@mui/material";
import { HouseBlock } from "./project-wizard/house-blocks/types";
import { GeneralInformationGroup } from "./project-wizard/house-blocks/general-information/GeneralInformationGroup";
import { MutationInformationGroup } from "./project-wizard/house-blocks/mutation-information/MutationInformationGroup";
import { useState } from "react";
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

type Props = {
    projectDetailHouseBlock?: HouseBlock;
    editForm: boolean;
};

export const BlockHousesForm = ({ projectDetailHouseBlock, editForm }: Props) => {
    const [projectForm, setProjectForm] = useState<HouseBlock>(projectDetailHouseBlock ? projectDetailHouseBlock : emptyHouseBlockForm);
    const [edit, setEdit] = useState(false);

    const oldForm = projectDetailHouseBlock && { ...projectDetailHouseBlock };
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
                                <SaveIcon sx={{ cursor: "pointer" }} onClick={() => console.log(projectForm.houseblockId)} />
                            </Tooltip>
                        </>
                    )}
                </Stack>
            )}
            <Grid container spacing={2} alignItems="stretch">
                <Grid item xs={12} md={8}>
                    <GeneralInformationGroup projectForm={projectForm} setProjectForm={setProjectForm} edit={edit} editForm={editForm} />
                </Grid>
                <Grid item xs={12} md={4}>
                    <MutationInformationGroup projectForm={projectForm} setProjectForm={setProjectForm} edit={edit} editForm={editForm} />
                </Grid>
            </Grid>
            <Grid container mt={2}>
                <Grid item xs={12}>
                    <OwnershipInformationGroup projectForm={projectForm} setProjectForm={setProjectForm} />
                </Grid>
            </Grid>
            <Grid container spacing={2} alignItems="stretch" mt={0.5}>
                <Grid item xs={12} md={4}>
                    <PhysicalAppeareanceGroup projectForm={projectForm} setProjectForm={setProjectForm} />
                </Grid>
                <Grid item xs={12} md={4}>
                    <PurposeGroup projectForm={projectForm} setProjectForm={setProjectForm} />
                </Grid>
                <Grid item xs={12} md={4}>
                    <HouseTypeGroup projectForm={projectForm} setProjectForm={setProjectForm} />
                </Grid>
            </Grid>
            <Grid container spacing={2} alignItems="stretch" mt={0.5}>
                <Grid item xs={12} md={8}>
                    <GroundPositionGroup projectForm={projectForm} setProjectForm={setProjectForm} />
                </Grid>
                <Grid item xs={12} md={4}>
                    <Programming projectForm={projectForm} setProjectForm={setProjectForm} />
                </Grid>
            </Grid>
        </Box>
    );
};
