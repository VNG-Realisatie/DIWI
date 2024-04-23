import { Typography } from "@mui/material";
import { RangeValue } from "../../../types/houseBlockTypes";
import { useTranslation } from "react-i18next";
import { formatMonetaryValue } from "./MonetaryRangeInput";

export const MonetaryRangeLabel = ({ value }: { value: RangeValue }) => {
    const { t } = useTranslation();
    if (value.value !== null) {
        return <Typography>€ {formatMonetaryValue(value.value)}</Typography>;
    } else if (value.max !== null && value.min !== null) {
        return (
            <Typography>
                € {formatMonetaryValue(value.min)} - € {formatMonetaryValue(value.max)}
            </Typography>
        );
    } else if (value.min !== null) {
        return (
            <Typography>
                € {formatMonetaryValue(value.min)} {t("generic.andMore")}
            </Typography>
        );
    } else if (value.max !== null) {
        return (
            <Typography>
                € {formatMonetaryValue(value.max)} {t("generic.andLess")}
            </Typography>
        );
    } else {
        return <></>;
    }
};

export default MonetaryRangeLabel;
