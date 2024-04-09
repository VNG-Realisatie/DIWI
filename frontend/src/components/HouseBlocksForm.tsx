import { Grid } from "@mui/material";
import { HouseBlock } from "../types/houseBlockTypes";
import { GeneralInformationGroup } from "./project-wizard/house-blocks/general-information/GeneralInformationGroup";
import { MutationInformationGroup } from "./project-wizard/house-blocks/mutation-information/MutationInformationGroup";
import { OwnershipInformationGroup } from "./project-wizard/house-blocks/ownership-information/OwnershipInformationGroup";
import { PhysicalAppeareanceGroup } from "./project-wizard/house-blocks/physical-appearence/PhysicalAppeareanceGroup";
import { PurposeGroup } from "./project-wizard/house-blocks/purpose/PurposeGroup";
import { HouseTypeGroup } from "./project-wizard/house-blocks/house-type/HouseTypeGroup";
import { GroundPositionGroup } from "./project-wizard/house-blocks/ground-position/GroundPositionGroup";
import { Programming } from "./project-wizard/house-blocks/programming/Programming";

import { CustomPropertiesGroup } from "./project-wizard/house-blocks/custom-properties/CustomPropertiesGroup";
import { useContext } from "react";
import HouseBlockContext from "../context/HouseBlockContext";
import { CustomPropertyValue } from "../api/customPropServices";
import { useTranslation } from "react-i18next";

type Props = {
    readOnly: boolean;
    houseBlock: HouseBlock;
    setHouseBlock: (hb: HouseBlock) => void;
};

export const HouseBlocksForm = ({ readOnly, houseBlock, setHouseBlock }: Props) => {
    const { t } = useTranslation();

    const { getCustomPropertyValues, updateCustomPropertyValues } = useContext(HouseBlockContext);

    const handleUpdateCustomPropertyValues = (customPropertyValues: CustomPropertyValue[]) => {
        if (houseBlock?.houseblockId) {
            updateCustomPropertyValues(houseBlock?.houseblockId, customPropertyValues);
        }
    };

    return (
        <>
            <Grid container spacing={2} alignItems="stretch">
                <Grid item xs={12} md={8}>
                    <GeneralInformationGroup houseBlock={houseBlock} setHouseBlock={setHouseBlock} readOnly={readOnly} />
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
                    <PurposeGroup houseBlock={houseBlock} setHouseBlock={setHouseBlock} readOnly={readOnly} />
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
                    {houseBlock.houseblockId && (
                        <CustomPropertiesGroup
                            {...{
                                readOnly,
                                customPropertyValues: houseBlock?.houseblockId ? getCustomPropertyValues(houseBlock?.houseblockId) : [],
                                setCustomPropertyValues: handleUpdateCustomPropertyValues,
                                columnTitleStyle: {},
                            }}
                        />
                    )}
                    {!houseBlock.houseblockId && <>{t("createProject.houseBlocksForm.saveBeforeCustomProps")}</>}
                </Grid>
            </Grid>
        </>
    );
};
