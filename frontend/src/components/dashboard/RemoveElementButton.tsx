import { Box, IconButton } from "@mui/material";
import CancelIcon from "@mui/icons-material/Cancel";

type Props = {
    handleHide: () => void;
};

export const RemoveElementButton = ({ handleHide }: Props) => {
    return (
        <Box style={{ position: "relative" }}>
            <IconButton
                onClick={handleHide}
                style={{
                    position: "absolute",
                    top: -20,
                    right: 8,
                    zIndex: 1,
                }}
            >
                <CancelIcon />
            </IconButton>
        </Box>
    );
};
