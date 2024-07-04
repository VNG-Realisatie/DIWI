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
            <PriceCategoriesTable
                row={priceRangeBuyCategories[0]?.ranges || []}
                property={priceRangeBuyCategories[0]}
                setRangeCategories={setPriceRangeBuyCategories}
            />
            <PriceCategoriesTable
                row={priceRangeRentCategories[0]?.ranges || []}
                property={priceRangeRentCategories[0]}
                setRangeCategories={setPriceRangeRentCategories}
            />
        </>
    );
};

export default PriceCategories;
