import { Box } from "@mui/material";
import { ReactNode } from "react";

type Props = {
    children: ReactNode;
};

export const CellContainer = ({ children }: Props) => {
    return <Box sx={{ border: "solid 1px #ddd", p: 0.5 }}>{children}</Box>;
};
