import { Box, Grid } from "@mui/material";
import {
    GroundPositionInformations,
    HouseBlock,
    HouseTypeInformations,
    MutationInformations,
    OwnershipSingleValue,
    PhysicalInformations,
    PurposeInformations,
} from "./project-wizard/house-blocks/types";
import { GeneralInformationGroup } from "./project-wizard/house-blocks/general-information/GeneralInformationGroup";
import { MutationInformationGroup } from "./project-wizard/house-blocks/mutation-information/MutationInformationGroup";
import { useEffect, useState } from "react";
import { OwnershipInformationGroup } from "./project-wizard/house-blocks/ownership-information/OwnershipInformationGroup";
import { PhysicalAppeareanceGroup } from "./project-wizard/house-blocks/physical-appearence/PhysicalAppeareanceGroup";
import { PurposeGroup } from "./project-wizard/house-blocks/purpose/PurposeGroup";
import { HouseTypeGroup } from "./project-wizard/house-blocks/house-type/HouseTypeGroup";
import { GroundPositionGroup } from "./project-wizard/house-blocks/ground-position/GroundPositionGroup";
import { Programming } from "./project-wizard/house-blocks/programming/Programming";

type Props = {
    projectForm: HouseBlock;
    setProjectForm(project: HouseBlock): void;
};

export const BlockHousesForm = ({ projectForm, setProjectForm }: Props) => {
    const [mutationInformationForm, setMutationInformationForm] = useState<MutationInformations>(projectForm.mutation);

    const [ownershipInformationForm, setOwershipInformationForm] = useState<OwnershipSingleValue[]>(projectForm.ownershipValue);

    const [physicalAppearanceForm, setPhysicalAppearanceForm] = useState<PhysicalInformations>(projectForm.physicalAppeareance);

    const [purposeForm, setPurposeForm] = useState<PurposeInformations>(projectForm.purpose);

    const [houseTypeForm, setHouseTypeForm] = useState<HouseTypeInformations>(projectForm.houseType);

    const [groundPositionForm, setGroundPositionForm] = useState<GroundPositionInformations>(projectForm.groundPosition);

    useEffect(() => {
        setProjectForm({
            ...projectForm,
            mutation: mutationInformationForm,
            physicalAppeareance: physicalAppearanceForm,
            purpose: purposeForm,
            houseType: houseTypeForm,
            groundPosition: groundPositionForm,
        });
    }, [groundPositionForm, houseTypeForm, mutationInformationForm, physicalAppearanceForm, projectForm, purposeForm, setProjectForm]);

    return (
        <Box mt={4}>
            <Grid container spacing={2} alignItems="stretch">
                <Grid item xs={12} md={8}>
                    <GeneralInformationGroup projectForm={projectForm} setProjectForm={setProjectForm} />
                </Grid>
                <Grid item xs={12} md={4}>
                    <MutationInformationGroup projectForm={mutationInformationForm} setProjectForm={setMutationInformationForm} />
                </Grid>
            </Grid>
            <Grid container mt={2}>
                <Grid item xs={12}>
                    <OwnershipInformationGroup projectForm={ownershipInformationForm} setProjectForm={setOwershipInformationForm} />
                </Grid>
            </Grid>
            <Grid container spacing={2} alignItems="stretch" mt={1}>
                <Grid item xs={12} md={4}>
                    <PhysicalAppeareanceGroup projectForm={physicalAppearanceForm} setProjectForm={setPhysicalAppearanceForm} />
                </Grid>
                <Grid item xs={12} md={4}>
                    <PurposeGroup projectForm={purposeForm} setProjectForm={setPurposeForm} />
                </Grid>
                <Grid item xs={12} md={4}>
                    <HouseTypeGroup projectForm={houseTypeForm} setProjectForm={setHouseTypeForm} />
                </Grid>
            </Grid>
            <Grid container spacing={2} alignItems="stretch" mt={1}>
                <Grid item xs={12} md={8}>
                    <GroundPositionGroup projectForm={groundPositionForm} setProjectForm={setGroundPositionForm} />
                </Grid>
                <Grid item xs={12} md={4}>
                    <Programming projectForm={projectForm} setProjectForm={setProjectForm} />
                </Grid>
            </Grid>
        </Box>
    );
};
