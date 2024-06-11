import { Typography } from "@mui/material";
import { useTranslation } from "react-i18next";
import { TooltipInfo } from "../../widgets/TooltipInfo";

type Props = {
    text: string;
    required: boolean;
    readOnly?: boolean;
    tooltipInfoText?: string;
};
export const LabelComponent = ({ text, required, readOnly = false, tooltipInfoText }: Props) => {
    const { t } = useTranslation();

    return (
        <Typography variant="subtitle1" fontWeight="500" fontStyle={readOnly ? "italic" : "normal"}>
            {t(text)}
            {required ? " *" : ""}
            {tooltipInfoText ? <TooltipInfo text={t(`${tooltipInfoText}title`)} /> : undefined}
        </Typography>
    );
};
