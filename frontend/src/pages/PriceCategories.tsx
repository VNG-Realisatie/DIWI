import PriceCategoriesTable from "../components/price-categories/PriceCategoriesTable";
import { Typography } from "@mui/material";
import { t } from "i18next";
import { observer } from "mobx-react-lite";
import { useCustomPropertyStore } from "../hooks/useCustomPropertyStore";
import { useEffect } from "react";

const PriceCategories = observer(() => {
    const { customProperties, fetchCustomProperties } = useCustomPropertyStore();
    const priceRangeRentCategories = customProperties.filter((property) => property.name === "priceRangeRent");
    const priceRangeBuyCategories = customProperties.filter((property) => property.name === "priceRangeBuy");

    useEffect(() => {
        fetchCustomProperties();
    }, [fetchCustomProperties]);

    if (priceRangeRentCategories.length === 0 || priceRangeBuyCategories.length === 0) return null;

    return (
        <>
            <Typography variant="h6" gutterBottom component="div">
                {t("admin.priceCategories.priceCategoryBuy")}
            </Typography>
            <PriceCategoriesTable property={priceRangeBuyCategories[0]} />
            <Typography variant="h6" gutterBottom component="div">
                {t("admin.priceCategories.priceCategoryRent")}
            </Typography>
            <PriceCategoriesTable property={priceRangeRentCategories[0]} />
        </>
    );
});

export default PriceCategories;
