import { Typography } from "@mui/material";
import { useTranslation } from "react-i18next";

type Props = {
    required: boolean;
    text: string;
};
export const LabelComponent = ({ required, text }: Props) => {
    const { t } = useTranslation();
    return (
        <Typography variant="subtitle1" fontWeight="500">
            {t(text)}
            {required ? " *" : ""}
        </Typography>
    );
};
