import { Typography } from "@mui/material";
import { useTranslation } from "react-i18next";

type Props = {
    text: string;
    required: boolean;
    readOnly?: boolean;
};
export const LabelComponent = ({ text, required, readOnly = false }: Props) => {
    const { t } = useTranslation();

    return (
        <Typography variant="subtitle1" fontWeight="500" fontStyle={readOnly ? "italic" : "normal"}>
            {t(text)}
            {required ? " *" : ""}
        </Typography>
    );
};
