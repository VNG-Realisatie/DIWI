import { Box } from "@mui/material";
import { ReactNode } from "react";
interface Props {
    children: ReactNode;
}
export const InputContainer = ({ children }: Props) => {
    return (
        <Box p={1} border="solid 1px #DDD" width="100%" height="100%">
            {children}
        </Box>
    );
};
