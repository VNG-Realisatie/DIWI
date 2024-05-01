import { Box, Typography } from "@mui/material";
import { ReactNode } from "react";

type Props = {
    children: ReactNode;
};

export const CellContainer = ({ children }: Props) => {
    return (
        <Box sx={{ border: "solid 1px #aaa", borderRadius: "5px", p: 0.5 }}>
            <Typography minHeight={30}>{children}</Typography>
        </Box>
    );
};
