import { Select, MenuItem } from "@mui/material";
import { useTranslation } from "react-i18next";
import { PropertyType, propertyType } from "../../types/enums";

type Props = {
    selectedPropertyType: PropertyType;
    setSelectedPropertyType: (propertyType: PropertyType) => void;
    disabled: boolean;
}

const PropertyTypeSelect = ({ selectedPropertyType, setSelectedPropertyType, disabled } : Props) => {
    const { t } = useTranslation();

    return (
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
