import { IconButton, Typography, useTheme } from "@mui/material";
import Add from "@mui/icons-material/Add";
import { useNavigate } from "react-router-dom";
import { useEffect, useRef } from "react";

type PlusButtonProps = {
    color: string;
    link: string | (() => void);
    text: string;
};

export default function PlusButton({ color, link, text }: PlusButtonProps) {
    const theme = useTheme();
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

    const handleButtonClick = () => {
        if (typeof link === "string") {
            navigate(link);
        } else {
            link();
        }
    };

    return (
        <IconButton
            ref={buttonRef}
            aria-label="add"
            size="large"
            onClick={handleButtonClick}
            sx={{
                position: "absolute",
                bottom: 50,
                right: 50,
                zIndex: 999,
                borderRadius: "40px",
                backgroundColor: color,
                color: theme.palette.common.white,
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
