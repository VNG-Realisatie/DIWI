import { Box, Grid } from "@mui/material";
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

type Props = {
    projectDetailHouseBlock?: HouseBlock;
};

export const BlockHousesForm = ({ projectDetailHouseBlock }: Props) => {
    const [projectForm, setProjectForm] = useState<HouseBlock>(projectDetailHouseBlock ? projectDetailHouseBlock : emptyHouseBlockForm);
    return (
        <Box mt={4}>
            <Grid container spacing={2} alignItems="stretch">
                <Grid item xs={12} md={8}>
                    <GeneralInformationGroup projectForm={projectForm} setProjectForm={setProjectForm} />
                </Grid>
                <Grid item xs={12} md={4}>
                    <MutationInformationGroup projectForm={projectForm} setProjectForm={setProjectForm} />
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
