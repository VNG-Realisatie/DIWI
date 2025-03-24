import { ExportProperty, GelderlandPriceCategories } from "../../api/exportServices";
import { Property } from "../../api/adminSettingServices";
import { Box, Grid } from "@mui/material";
import { LabelComponent } from "../project/LabelComponent";
import CategoryInput from "../project/inputs/CategoryInput";
import { t } from "i18next";
import { CustomPropertyWidget } from "../CustomPropertyWidget";
import { ObjectType } from "../../types/enums";

type Props = {
    priceCategories: GelderlandPriceCategories | undefined;
    customProperties: Property[];
    setPriceCategories: (priceCategories: GelderlandPriceCategories) => void;
    mapPropertyToCustomDefinition: (property: ExportProperty, selectedProperty: Property) => Property;
};

const ExportMappingPriceCategories = ({ priceCategories, customProperties, setPriceCategories, mapPropertyToCustomDefinition }: Props) => {
    if (!priceCategories) return null;
    const rentCustomProperties = customProperties.filter((property) => property.name === "priceRangeRent");
    const buyCustomProperties = customProperties.filter((property) => property.name === "priceRangeBuy");

    const handleSetCustomValue = (type: "rent" | "buy", index: number, newValue: { categories: string[] }) => {
        const updatedCategories = [...priceCategories[type]];
        updatedCategories[index].categoryValueIds = newValue.categories;
        setPriceCategories({ ...priceCategories, [type]: updatedCategories });
    };

    const renderCategorySection = (type: "rent" | "buy", customProperties: Property[]) => {
        return (
            <Grid item xs={12}>
                <CategoryInput
                    readOnly={true}
                    mandatory={false}
                    options={[]}
                    values={{ id: "", name: t(`exchangeData.priceCategories.${type}`) }}
                    setValue={() => {}}
                    multiple={false}
                />

                {priceCategories[type].map((category, index) => {
                    if (!customProperties[0].ranges) return;

                    const extendedCategory = {
                        ...category,
                        objectType: "PROJECT" as ObjectType,
                        mandatory: false,
                        propertyTypes: ["CATEGORY"],
                        singleSelect: false,
                    };

                    return (
                        <Box key={index} marginLeft={10}>
                            <LabelComponent text={category.name} required={false} disabled={false} />
                            <CustomPropertyWidget
                                readOnly={false}
                                customValue={category && category.categoryValueIds && { categories: category.categoryValueIds }}
                                customDefinition={mapPropertyToCustomDefinition(extendedCategory, customProperties[0])}
                                setCustomValue={(newValue) => handleSetCustomValue(type, index, { categories: newValue.categories || [] })}
                                isExportPage={true}
                            />
                        </Box>
                    );
                })}
            </Grid>
        );
    };

    return (
        <>
            {renderCategorySection("rent", rentCustomProperties)}
            {renderCategorySection("buy", buyCustomProperties)}
        </>
    );
};

export default ExportMappingPriceCategories;
