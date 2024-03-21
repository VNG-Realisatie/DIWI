import { Box, Typography } from "@mui/material";
import { ReactNode } from "react";

type Props = {
    children: ReactNode;
};

export const CellContainer = ({ children }: Props) => {
    return (
        <Box sx={{ border: "solid 1px #ddd", p: 0.5 }}>
            <Typography minHeight={34}>{children}</Typography>
        </Box>
    );
};
