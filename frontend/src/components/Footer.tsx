import { Box, Stack } from "@mui/material";
import diwilogo from "../assets/diwilogo.png";
import vnglogo from "../assets/vnglogo.png";
export const Footer = () => {
    return (
        <Stack
            position="fixed"
            bottom="0"
            height="54px"
            sx={{ backgroundColor: "#002C64", width: "100%", zIndex: 1000 }}
            direction="row"
            justifyContent="center"
        >
            <Box display="flex" alignItems="center" justifyContent="space-between" color="#FFFFFF">
                <Box display="flex" sx={{ backgroundColor: "#FFFFFF", borderRadius: "15px" }} p={0.5} mr={1}>
                    <img src={diwilogo} alt="diwilogo" />
                </Box>
                <span>powered by </span>
                <Box display="flex" justifyContent="center" sx={{ backgroundColor: "#FFFFFF", borderRadius: "15px" }} ml={1}>
                    <img src={vnglogo} alt="vnglogo" />
                </Box>
            </Box>
        </Stack>
    );
};
