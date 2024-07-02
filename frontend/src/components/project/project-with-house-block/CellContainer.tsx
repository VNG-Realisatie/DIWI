import { Box} from "@mui/material";
import { ReactNode } from "react";

type Props = {
    children: ReactNode;
};

export const CellContainer = ({ children }: Props) => {
    return (
        <Box sx={{ border: "solid 1px #aaa", borderRadius: "5px", p: 0.5, minHeight: "47px" }} display="flex" alignItems="center">
            <Box minHeight={30}>{children}</Box>
        </Box>
    );
};
