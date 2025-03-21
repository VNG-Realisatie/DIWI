import React from "react";
import { GelderlandPriceCategories } from "../../api/exportServices";
import { Property } from "../../api/adminSettingServices";
import { Box, Grid } from "@mui/material";
import { LabelComponent } from "../project/LabelComponent";
import CategoryInput from "../project/inputs/CategoryInput";
import { t } from "i18next";

type Props = {
    priceCategories: GelderlandPriceCategories;
    customProperties: Property[];
    setPriceCategories: (priceCategories: GelderlandPriceCategories) => void;
};

const ExportMappingPriceCategories = ({ priceCategories, customProperties, setPriceCategories }: Props) => {
    const rentCustomProperties = customProperties.filter((property) => property.name === "priceRangeRent");
    const buyCustomProperties = customProperties.filter((property) => property.name === "priceRangeBuy");

    const handleSetValue = (index: number) => (selectedIds: string[]) => {
        console.log("handleSetValue called with selectedIds:", selectedIds);
        const updatedRentCategories = [...priceCategories.rent];
        updatedRentCategories[index].categoryValueIds = selectedIds;
        setPriceCategories({
            ...priceCategories,
            rent: updatedRentCategories,
        });
    };

    return (
        <>
            <Grid item xs={12}>
                <CategoryInput readOnly={true} mandatory={false} options={[]} values={{ id: "", name: "rent" }} setValue={() => {}} multiple={false} />

                {priceCategories.rent.map((category, index) => {
                    if (!rentCustomProperties[0].ranges) return;
                    return (
                        <Box key={index} marginLeft={10}>
                            <LabelComponent text={category.name} required={false} disabled={false} />
                            <CategoryInput
                                readOnly={false}
                                mandatory={false}
                                options={rentCustomProperties[0].ranges}
                                values={category.categoryValueIds}
                                setValue={() => handleSetValue(index)}
                                multiple={true}
                            />
                        </Box>
                    );
                })}
            </Grid>
            <Grid item xs={12}>
                <CategoryInput
                    readOnly={true}
                    mandatory={false}
                    options={buyCustomProperties}
                    values={{ id: "", name: "buy" }}
                    setValue={() => {}}
                    multiple={false}
                />
            </Grid>
        </>
    );
};

export default ExportMappingPriceCategories;
