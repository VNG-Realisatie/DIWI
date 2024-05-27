import { IconButton, Typography } from "@mui/material";
import Add from "@mui/icons-material/Add";
import { useNavigate } from "react-router-dom";
import { useEffect, useRef } from "react";
import { useTranslation } from "react-i18next";
import { theme } from "../theme";
import * as Paths from "../Paths";
import useAllowedActions from "../hooks/useAllowedActions";

type PlusButtonProps = {
    color: string | undefined;
    link: string | (() => void);
    text: string;
};

function PlusButton({ color, link, text }: PlusButtonProps) {
    const navigate = useNavigate();
    const textWidth = text ? text.length * 8 : 0;
    const buttonRef = useRef<HTMLButtonElement>(null);

    useEffect(() => {
        if (buttonRef.current) {
            buttonRef.current.classList.add("PlusButtonHovered");
            const timeoutId = setTimeout(() => {
                buttonRef.current?.classList.remove("PlusButtonHovered");
            }, 2000);
            return () => clearTimeout(timeoutId);
        }
    }, []);

    const handleButtonClick = () => (typeof link === "string" ? navigate(link) : link());

    return (
        <IconButton
            ref={buttonRef}
            aria-label="add"
            size="large"
            onClick={handleButtonClick}
            sx={{
                position: "absolute",
                bottom: 20,
                right: 20,
                zIndex: 999,
                borderRadius: "40px",
                backgroundColor: color,
                color: theme.palette.common.white,
                boxShadow: "0px 4px 6px rgba(0, 0, 0, 0.2)",
                transition: "padding-left 0.3s ease, background-color 0.3s ease",
                "&:hover, &.PlusButtonHovered": {
                    paddingLeft: `${textWidth + 30}px`,
                    borderRadius: "40px",
                    backgroundColor: color,
                    "& > .MuiButton-label": {
                        justifyContent: "flex-end",
                    },
                    "& > .PlusButtonText": {
                        visibility: "visible",
                        position: "absolute",
                        left: "20px",
                        maxWidth: `${textWidth}px`,
                        whiteSpace: "nowrap",
                        overflow: "hidden",
                        width: `${textWidth}px`,
                        transition: "width 0.3s ease",
                    },
                },
                "& > .PlusButtonText": {
                    visibility: "hidden",
                    position: "absolute",
                    width: 0,
                    left: "20px",
                    whiteSpace: "nowrap",
                    overflow: "hidden",
                    transition: "width 0.3s ease, visibility 0.3s ease",
                },
            }}
        >
            <Add sx={{ fontSize: 40, stroke: color, strokeWidth: "0.8px" }} />
            {<Typography className="PlusButtonText">{text}</Typography>}
        </IconButton>
    );
}

export const AddHouseBlockButton: React.FC<{ onClick: () => void }> = ({ onClick }) => {
    const allowedActions = useAllowedActions();
    const { t } = useTranslation();
    const handleClick = () => {
        onClick();
    };

    const buttonProps: PlusButtonProps = {
        color: theme.palette.primary.customLightBlue,
        link: handleClick,
        text: t("projectDetail.createNewHouseBlock"),
    };

    return allowedActions.includes("CREATE_NEW_PROJECT") ? <PlusButton {...buttonProps} /> : null;
};

export const AddProjectButton: React.FC = () => {
    const allowedActions = useAllowedActions();
    const { t } = useTranslation();
    const buttonProps: PlusButtonProps = {
        color: theme.palette.primary.customDarkBlue,
        link: Paths.projectWizard.path,
        text: t("projects.createNewProject"),
    };

    return allowedActions.includes("CREATE_NEW_PROJECT") ? <PlusButton {...buttonProps} /> : null;
};
