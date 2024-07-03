import { Typography } from "@mui/material";
import { useTranslation } from "react-i18next";
import { TooltipInfo } from "../../widgets/TooltipInfo";

type Props = {
    text: string;
    required: boolean;
    tooltipInfoText?: string;
};
export const LabelComponent = ({ text, required, tooltipInfoText }: Props) => {
    const { t } = useTranslation();

    return (
        <Typography variant="subtitle1" fontWeight="500" fontStyle={"normal"}>
            {text}
            {required ? " *" : ""}
            {tooltipInfoText ? <TooltipInfo text={t(tooltipInfoText)} /> : undefined}
        </Typography>
    );
};
