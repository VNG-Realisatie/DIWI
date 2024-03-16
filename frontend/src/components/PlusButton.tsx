import { IconButton, Typography, useTheme } from "@mui/material";
import Add from "@mui/icons-material/Add";
import React from "react";

type PlusButtonProps = {
    color: any;
    link: string;
    text: string;
};

export default function PlusButton({ color, link, text }: PlusButtonProps) {
    const theme = useTheme();

    const textWidth = text ? text.length * 8 : 0;

    return (
        <IconButton
            aria-label="add"
            size="large"
            sx={{
                position: "absolute",
                bottom: 50,
                right: 50,
                zIndex: 9999,
                borderRadius: "40px",
                backgroundColor: theme.palette.info.main,
                color: theme.palette.common.white,
                transition: "padding-left 0.3s ease, background-color 0.3s ease",
                "&:hover": {
                    paddingLeft: `${textWidth + 30}px`,
                    borderRadius: "40px",
                    backgroundColor: theme.palette.info.main,
                    "& > .MuiButton-label": {
                        justifyContent: "flex-end",
                    },
                    "& > .text": {
                        display: "block",
                        position: "absolute",
                        left: "30px",
                        paddingRight: "2px",
                    },
                },
                "& > .text": {
                    display: "none",
                    position: "absolute",
                },
            }}
        >
            <Add fontSize="large" />
            {text && <Typography className="text">{text}</Typography>}
        </IconButton>
    );
}
