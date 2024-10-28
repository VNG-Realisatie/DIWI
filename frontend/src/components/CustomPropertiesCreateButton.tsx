import { Button } from "@mui/material";
import { ImportErrorType } from "./ImportErrors";
import { addCustomProperty, getCustomProperties, getCustomProperty, Property, updateCustomProperty } from "../api/adminSettingServices";
import { useContext, useEffect, useState } from "react";
import AlertContext from "../context/AlertContext";
import { t } from "i18next";

type Props = {
    error: ImportErrorType;
};
type ErrorMappings = {
    [key: string]: {
        id?: string;
        body: Property;
    };
};

const property: Property = {
    id: "",
    name: "",
    type: "CUSTOM",
    objectType: "PROJECT",
    propertyType: "TEXT",
    disabled: false,
    categories: undefined,
    ordinals: undefined,
    mandatory: false,
    singleSelect: undefined,
    ranges: undefined,
};
export default function CustomPropertiesCreateButton({ error }: Props) {
    const { setAlert } = useContext(AlertContext);
    const [isButtonDisabled, setIsButtonDisabled] = useState(false);
    const [categoricalProperty, setCategoricalProperty] = useState<Property | null>(null);

    const [priceRangeRentCategories, setPriceRangeRentCategories] = useState<Property[]>([]);
    const [priceRangeBuyCategories, setPriceRangeBuyCategories] = useState<Property[]>([]);

    useEffect(() => {
        getCustomProperties().then((customProperties) => {
            const rentCategories = customProperties.filter((property) => property.name === "priceRangeRent");
            const buyCategories = customProperties.filter((property) => property.name === "priceRangeBuy");
            setPriceRangeRentCategories(rentCategories);
            setPriceRangeBuyCategories(buyCategories);
        });
    }, [setPriceRangeRentCategories, setPriceRangeBuyCategories]);

    useEffect(() => {
        const fetchCustomProperty = async () => {
            if (error.customPropertyId) {
                const property = await getCustomProperty(error.customPropertyId);
                setCategoricalProperty(property);
            }
        };

        fetchCustomProperty();
    }, [error.customPropertyId]);

    const errorMappings: ErrorMappings = {
        unknown_houseblock_numeric_property: {
            body: { ...property, name: error.value || "", objectType: "WONINGBLOK", propertyType: "NUMERIC" },
        },
        unknown_houseblock_property: {
            body: { ...property, name: error.value || "", objectType: "WONINGBLOK", propertyType: "TEXT" },
        },
        unknown_project_property: {
            body: { ...property, name: error.value || "", objectType: "PROJECT", propertyType: "TEXT" },
        },
        unknown_project_category_property: {
            body: { ...property, name: error.value || "", objectType: "PROJECT", propertyType: "CATEGORY", categories: [], singleSelect: false },
        },
        unknown_price_rent_range_category: {
            id: priceRangeRentCategories[0]?.id,
            body: {
                ...property,
                name: "priceRangeRent",
                objectType: "WONINGBLOK",
                propertyType: "RANGE_CATEGORY",
                ranges: [
                    ...(priceRangeRentCategories[0]?.ranges || []),
                    {
                        name: error.value || "",
                        min: 0,
                        id: "",
                        disabled: false,
                    },
                ],
            },
        },
        unknown_price_buy_range_category: {
            id: priceRangeBuyCategories[0]?.id,
            body: {
                ...property,
                name: "priceRangeBuy",
                propertyType: "RANGE_CATEGORY",
                ranges: [
                    ...(priceRangeBuyCategories[0]?.ranges || []),
                    {
                        name: error.value || "",
                        min: 0,
                        id: "",
                        disabled: false,
                    },
                ],
            },
        },
        unknown_property_value: {
            id: categoricalProperty?.id,
            body: {
                ...property,
                name: categoricalProperty?.name || "",
                objectType: categoricalProperty?.objectType || "PROJECT",
                propertyType: "CATEGORY",
                categories: [
                    ...(categoricalProperty?.categories || []),
                    {
                        name: error.value || "",
                        id: "",
                        disabled: false,
                    },
                ],
                singleSelect: false,
            },
        },
    };

    const mapping = errorMappings[error.errorCode as keyof typeof errorMappings];
    const handleClick = async () => {
        if (mapping) {
            try {
                mapping.id ? await updateCustomProperty(mapping.id, mapping.body) : await addCustomProperty(mapping.body);
                setAlert(t("admin.settings.notifications.successfullySaved"), "success");
                setIsButtonDisabled(true);
            } catch (error: unknown) {
                if (error instanceof Error) {
                    setAlert(error.message, "error");
                }
            }
        }
    };

    const shouldRenderButton = Object.keys(errorMappings).includes(error.errorCode);

    return shouldRenderButton ? (
        <Button variant="contained" color="primary" onClick={handleClick} disabled={isButtonDisabled}>
            {mapping.id ? t("admin.settings.update") : t("admin.settings.create")}
        </Button>
    ) : null;
}
