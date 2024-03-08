import { Box } from "@mui/material";
import { ReactNode } from "react";
interface Props {
    children: ReactNode;
}
export const WizardCard = ({ children }: Props) => {
    return (
        <Box p={2} bgcolor="#F0F0F0" width="100%">
            {children}
        </Box>
    );
};
