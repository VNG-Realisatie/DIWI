import { InputAdornment, Typography } from "@mui/material";
import { t } from "i18next";

type ClearInputAdornmentProps = {
    onClick: () => void;
};

const ClearInputAdornment = ({ onClick }: ClearInputAdornmentProps) => (
    <InputAdornment onClick={onClick} position="end" sx={{ ":hover": { cursor: "pointer" } }}>
        <Typography fontStyle="italic" fontSize={12} sx={{ textDecoration: "underline" }} data-testid="clear-input">
            {t("createProject.clearInput")}
        </Typography>
    </InputAdornment>
);

export default ClearInputAdornment;
