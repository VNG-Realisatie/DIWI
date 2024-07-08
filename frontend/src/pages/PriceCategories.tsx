import PriceCategoriesTable from "../components/price-categories/PriceCategoriesTable";
import { useEffect, useState } from "react";
import { getCustomProperties, Property } from "../api/adminSettingServices";
import { Typography } from "@mui/material";
import { t } from "i18next";

const PriceCategories = () => {
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

    if (priceRangeRentCategories.length === 0 || priceRangeBuyCategories.length === 0) return null;

    return (
        <>
            <Typography variant="h6" gutterBottom component="div">
                {t("admin.priceCategories.priceCategoryBuy")}
            </Typography>
            <PriceCategoriesTable property={priceRangeBuyCategories[0]} setRangeCategories={setPriceRangeBuyCategories} />
            <Typography variant="h6" gutterBottom component="div">
                {t("admin.priceCategories.priceCategoryRent")}
            </Typography>
            <PriceCategoriesTable property={priceRangeRentCategories[0]} setRangeCategories={setPriceRangeRentCategories} />
        </>
    );
};

export default PriceCategories;
