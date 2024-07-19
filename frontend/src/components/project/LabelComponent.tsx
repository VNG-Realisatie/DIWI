import { Typography } from "@mui/material";
import { useTranslation } from "react-i18next";
import { TooltipInfo } from "../../widgets/TooltipInfo";

type Props = {
    text: string;
    required: boolean;
    tooltipInfoText?: string;
    disabled?: boolean;
};
export const LabelComponent = ({ text, required, tooltipInfoText, disabled }: Props) => {
    const { t } = useTranslation();
    const labelStyling = {
        color: "#000000",
        fontStyle: disabled ? "italic" : "normal",
    };
    return (
        <Typography variant="subtitle1" fontWeight="500" fontStyle={"normal"} sx={labelStyling}>
            {text}
            {required ? " *" : ""}
            {tooltipInfoText ? <TooltipInfo text={t(tooltipInfoText)} /> : undefined}
        </Typography>
    );
};
