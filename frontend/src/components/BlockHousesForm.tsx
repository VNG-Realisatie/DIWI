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
import { useState } from "react";
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
    const [mutationInformationForm, setMutationInformationForm] = useState<MutationInformations>({
        mutationKind: projectForm.mutation.mutationKind,
        grossPlanCapacity: projectForm.mutation.grossPlanCapacity,
        netPlanCapacity: projectForm.mutation.netPlanCapacity,
        demolition: projectForm.mutation.demolition,
    });
    const [ownershipInformationForm, setOwershipInformationForm] = useState<OwnershipSingleValue[]>([
        {
            type: "KOOPWONING",
            amount: null,
            value: { value: null, min: null, max: null },
            rentalValue: { value: null, min: null, max: null },
        },
        {
            type: "HUURWONING_PARTICULIERE_VERHUURDER",
            amount: null,
            value: { value: null, min: null, max: null },
            rentalValue: { value: null, min: null, max: null },
        },
        {
            type: "HUURWONING_WONINGCORPORATIE",
            amount: null,
            value: { value: null, min: null, max: null },
            rentalValue: { value: null, min: null, max: null },
        },
    ]);

    const [physicalAppearanceForm, setPhysicalAppearanceForm] = useState<PhysicalInformations>({
        tussenwoning: null,
        tweeondereenkap: null,
        portiekflat: null,
        hoekwoning: null,
        vrijstaand: null,
        gallerijflat: null,
    });

    const [purposeForm, setPurposeForm] = useState<PurposeInformations>({
        regular: null,
        youth: null,
        student: null,
        elderly: null,
        largeFamilies: null,
        ghz: null,
    });

    const [houseTypeForm, setHouseTypeForm] = useState<HouseTypeInformations>({
        meergezinswoning: null,
        eengezinswoning: null,
    });

    const [groundPositionForm, setGroundPositionForm] = useState<GroundPositionInformations>({
        noPermissionOwner: null,
        intentionPermissionOwner: null,
        formalPermissionOwner: null,
    });

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
