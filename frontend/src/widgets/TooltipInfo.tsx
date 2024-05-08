import { Tooltip } from "@mui/material";
import InfoIcon from "@mui/icons-material/Info";

type Props = {
    text: string;
};

export const TooltipInfo = ({ text }: Props) => {
    return (
        <Tooltip placement="top" arrow disableInteractive title={text} sx={{ whiteSpace: "pre-line" }}>
            <InfoIcon
                sx={{
                    marginLeft: "6px",
                    marginBottom: "-6px",
                }}
            />
        </Tooltip>
    );
};
