import { Tooltip } from "@mui/material";
import InfoIcon from "@mui/icons-material/Info";
import React from "react";

type Props = {
    text: string;
    children?: React.ReactNode;
};

export const TooltipInfo = ({ text, children }: Props) => {
    return (
        <Tooltip placement="top" arrow disableInteractive title={<span style={{ whiteSpace: "pre-line" }}>{text}</span>}>
            <span>
                {children}
                {!children && (
                    <InfoIcon
                        sx={{
                            marginLeft: "6px",
                            marginBottom: "-6px",
                        }}
                    />
                )}
            </span>
        </Tooltip>
    );
};
