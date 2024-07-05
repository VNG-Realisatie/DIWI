import PriceCategoriesTable from "../components/price-categories/PriceCategoriesTable";
import { useEffect, useState } from "react";
import { getCustomProperties, Property } from "../api/adminSettingServices";

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

    return (
        <>
            <PriceCategoriesTable property={priceRangeBuyCategories[0]} setRangeCategories={setPriceRangeBuyCategories} />
            <PriceCategoriesTable property={priceRangeRentCategories[0]} setRangeCategories={setPriceRangeRentCategories} />
        </>
    );
};

export default PriceCategories;
