import { Typography } from "@mui/material";
import { RangeValue } from "../../../types/houseBlockTypes";
import { useTranslation } from "react-i18next";

export const NumericRangeLabel = ({ value }: { value: RangeValue }) => {
    const { t } = useTranslation();
    if (value.value !== null) {
        return <Typography>{value.value}</Typography>;
    } else if (value.max !== null && value.min !== null) {
        return (
            <Typography>
                {value.min} - {value.max}
            </Typography>
        );
    } else if (value.min !== null) {
        return (
            <Typography>
                {value.min} {t("generic.andMore")}
            </Typography>
        );
    } else if (value.max !== null) {
        return (
            <Typography>
                {value.max} {t("generic.andLess")}
            </Typography>
        );
    } else {
        return <></>;
    }
};
