import { Grid } from "@mui/material";
import { HouseBlockWithCustomProperties } from "../types/houseBlockTypes";
import { GeneralInformationGroup } from "./project-wizard/house-blocks/general-information/GeneralInformationGroup";
import { MutationInformationGroup } from "./project-wizard/house-blocks/mutation-information/MutationInformationGroup";
import { OwnershipInformationGroup } from "./project-wizard/house-blocks/ownership-information/OwnershipInformationGroup";
import { PhysicalAppeareanceGroup } from "./project-wizard/house-blocks/physical-appearence/PhysicalAppeareanceGroup";
import { TargetGroup } from "./project-wizard/house-blocks/targetGroup/TargetGroup";
import { HouseTypeGroup } from "./project-wizard/house-blocks/house-type/HouseTypeGroup";
import { GroundPositionGroup } from "./project-wizard/house-blocks/ground-position/GroundPositionGroup";
import { Programming } from "./project-wizard/house-blocks/programming/Programming";

import { CustomPropertiesGroup } from "./project-wizard/house-blocks/custom-properties/CustomPropertiesGroup";
import { DateValidationErrors } from "../pages/ProjectWizardBlocks";

type Props = {
    readOnly: boolean;
    houseBlock: HouseBlockWithCustomProperties;
    setHouseBlock: (hb: HouseBlockWithCustomProperties) => void;
    errors?: DateValidationErrors;
};

export const HouseBlocksForm = ({ readOnly, houseBlock, setHouseBlock, errors }: Props) => {
    return (
        <>
            <Grid container spacing={2} alignItems="stretch">
                <Grid item xs={12} md={8}>
                    <GeneralInformationGroup
                        houseBlock={houseBlock}
                        setHouseBlock={setHouseBlock}
                        readOnly={readOnly}
                        errors={errors ? errors : { startDateError: null, endDateError: null }}
                    />
                </Grid>
                <Grid item xs={12} md={4}>
                    <MutationInformationGroup houseBlock={houseBlock} setHouseBlock={setHouseBlock} readOnly={readOnly} />
                </Grid>
            </Grid>
            <Grid container mt={2}>
                <Grid item xs={12}>
                    <OwnershipInformationGroup houseBlock={houseBlock} setHouseBlock={setHouseBlock} readOnly={readOnly} />
                </Grid>
            </Grid>
            <Grid container spacing={2} alignItems="stretch" mt={0.5}>
                <Grid item xs={12} md={4}>
                    <PhysicalAppeareanceGroup houseBlock={houseBlock} setHouseBlock={setHouseBlock} readOnly={readOnly} />
                </Grid>
                <Grid item xs={12} md={4}>
                    <TargetGroup houseBlock={houseBlock} setHouseBlock={setHouseBlock} readOnly={readOnly} />
                </Grid>
                <Grid item xs={12} md={4}>
                    <HouseTypeGroup houseBlock={houseBlock} setHouseBlock={setHouseBlock} readOnly={readOnly} />
                </Grid>
            </Grid>
            <Grid container spacing={2} alignItems="stretch" mt={0.5}>
                <Grid item xs={12} md={8}>
                    <GroundPositionGroup houseBlock={houseBlock} setHouseBlock={setHouseBlock} readOnly={readOnly} />
                </Grid>
                <Grid item xs={12} md={4}>
                    <Programming houseBlock={houseBlock} setHouseBlock={setHouseBlock} readOnly={readOnly} />
                </Grid>
            </Grid>
            <Grid container spacing={2} alignItems="stretch" mt={0.5}>
                <Grid item xs={12}>
                    <CustomPropertiesGroup
                        {...{
                            readOnly,
                            customPropertyValues: houseBlock?.customProperties ?? [],
                            setCustomPropertyValues: (updatedValues) => setHouseBlock({ ...houseBlock, customProperties: updatedValues }),
                            columnTitleStyle: {},
                        }}
                    />
                </Grid>
            </Grid>
        </>
    );
};
