import { Grid } from "@mui/material";
import { HouseBlock } from "./project-wizard/house-blocks/types";
import { GeneralInformationGroup } from "./project-wizard/house-blocks/general-information/GeneralInformationGroup";
import { MutationInformationGroup } from "./project-wizard/house-blocks/mutation-information/MutationInformationGroup";
import { useEffect, useState } from "react";
import { OwnershipInformationGroup } from "./project-wizard/house-blocks/ownership-information/OwnershipInformationGroup";
import { PhysicalAppeareanceGroup } from "./project-wizard/house-blocks/physical-appearence/PhysicalAppeareanceGroup";
import { PurposeGroup } from "./project-wizard/house-blocks/purpose/PurposeGroup";
import { HouseTypeGroup } from "./project-wizard/house-blocks/house-type/HouseTypeGroup";
import { GroundPositionGroup } from "./project-wizard/house-blocks/ground-position/GroundPositionGroup";
import { Programming } from "./project-wizard/house-blocks/programming/Programming";
import { emptyHouseBlockForm } from "./project-wizard/house-blocks/constants";

import { CustomPropertiesGroup } from "./project-wizard/house-blocks/custom-properties/CustomPropertiesGroup";
import { CustomPropertyValue, getBlockCustomPropertyValues } from "../api/customPropServices";

type Props = {
    projectDetailHouseBlock?: HouseBlock;
    readOnly: boolean;
    createFormHouseBlock: HouseBlock;
    setCreateFormHouseBlock: (hb: HouseBlock) => void;
    validationError?: string;
};

export const HouseBlocksForm = ({ projectDetailHouseBlock, readOnly, createFormHouseBlock, setCreateFormHouseBlock }: Props) => {
    const [projectForm, setProjectForm] = useState<HouseBlock>(projectDetailHouseBlock ? projectDetailHouseBlock : emptyHouseBlockForm);
    const [customValues, setCustomValues] = useState<CustomPropertyValue[]>([]);

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

    const defineProjectState = () => {
        if (!readOnly) {
            return projectForm;
        } else {
            return createFormHouseBlock;
        }
    };

    const defineProjectUpdateState = () => {
        if (!readOnly) {
            return setProjectForm;
        } else {
            return setCreateFormHouseBlock;
        }
    };

    return (
        <>
            <Grid container spacing={2} alignItems="stretch">
                <Grid item xs={12} md={8}>
                    <GeneralInformationGroup projectForm={defineProjectState()} setProjectForm={defineProjectUpdateState()} readOnly={readOnly} />
                </Grid>
                <Grid item xs={12} md={4}>
                    <MutationInformationGroup projectForm={defineProjectState()} setProjectForm={defineProjectUpdateState()} readOnly={readOnly} />
                </Grid>
            </Grid>
            <Grid container mt={2}>
                <Grid item xs={12}>
                    <OwnershipInformationGroup projectForm={defineProjectState()} setProjectForm={defineProjectUpdateState()} readOnly={readOnly} />
                </Grid>
            </Grid>
            <Grid container spacing={2} alignItems="stretch" mt={0.5}>
                <Grid item xs={12} md={4}>
                    <PhysicalAppeareanceGroup projectForm={defineProjectState()} setProjectForm={defineProjectUpdateState()} readOnly={readOnly} />
                </Grid>
                <Grid item xs={12} md={4}>
                    <PurposeGroup projectForm={defineProjectState()} setProjectForm={defineProjectUpdateState()} readOnly={readOnly} />
                </Grid>
                <Grid item xs={12} md={4}>
                    <HouseTypeGroup projectForm={defineProjectState()} setProjectForm={defineProjectUpdateState()} readOnly={readOnly} />
                </Grid>
            </Grid>
            <Grid container spacing={2} alignItems="stretch" mt={0.5}>
                <Grid item xs={12} md={8}>
                    <GroundPositionGroup projectForm={defineProjectState()} setProjectForm={defineProjectUpdateState()} readOnly={readOnly} />
                </Grid>
                <Grid item xs={12} md={4}>
                    <Programming projectForm={defineProjectState()} setProjectForm={defineProjectUpdateState()} readOnly={readOnly} />
                </Grid>
            </Grid>
            <Grid container spacing={2} alignItems="stretch" mt={0.5}>
                <Grid item xs={12}>
                    <CustomPropertiesGroup {...{ readOnly, customValues, setCustomValues, columnTitleStyle: {} }} />
                </Grid>
            </Grid>
        </>
    );
};
