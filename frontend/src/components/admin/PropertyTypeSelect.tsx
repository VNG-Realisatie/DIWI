import { Select, MenuItem } from "@mui/material";
import { useTranslation } from "react-i18next";
import { PropertyType, propertyType } from "../../types/enums";
import { useEffect } from "react";

type Props = {
    selectedPropertyType: PropertyType;
    setSelectedPropertyType: (propertyType: PropertyType) => void;
    disabled: boolean;
    error?: string;
};

const PropertyTypeSelect = ({ selectedPropertyType, setSelectedPropertyType, disabled, error }: Props) => {
    const { t } = useTranslation();

    useEffect(() => {
        if (error === "unknown_project_category_property") {
            setSelectedPropertyType("CATEGORY" as PropertyType);
        } else if (error === "unknown_houseblock_numeric_property") {
            setSelectedPropertyType("NUMERIC" as PropertyType);
        }
    }, [error, setSelectedPropertyType]);


    return error === "unknown_project_category_property" || error === "unknown_houseblock_numeric_property" ? (
        <Select
            size="small"
            disabled={true}
            value={error === "unknown_project_category_property" ? "CATEGORY" : "NUMERIC"}
            labelId="propertyType"
            onChange={(e) => setSelectedPropertyType(e.target.value as PropertyType)}
        >
            {propertyType
                .filter((p) => p !== "RANGE_CATEGORY")
                .map((property) => (
                    <MenuItem key={property} value={property}>
                        {t(`admin.settings.propertyType.${property}`)}
                    </MenuItem>
                ))}
        </Select>
    ) : (
        <Select
            size="small"
            disabled={disabled}
            value={selectedPropertyType}
            labelId="propertyType"
            onChange={(e) => setSelectedPropertyType(e.target.value as PropertyType)}
        >
            {propertyType
                .filter((p) => p !== "RANGE_CATEGORY")
                .map((property) => (
                    <MenuItem key={property} value={property}>
                        {t(`admin.settings.propertyType.${property}`)}
                    </MenuItem>
                ))}
        </Select>
    );
};

export default PropertyTypeSelect;
