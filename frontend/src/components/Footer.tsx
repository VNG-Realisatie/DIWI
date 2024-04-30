import { Box, Stack } from "@mui/material";
import diwilogo from "../assets/diwilogo.png";
import vnglogo from "../assets/vnglogo.png";
import useLoading from "../hooks/useLoading";
import LoadingBar from "./LoadingBar";

export const Footer = () => {
    const { loading } = useLoading();
    const loadingHeight = 13;
    const footerHeight = 54;

    return (
        <Stack position="fixed" bottom="0" width="100%" height={footerHeight + (loading ? loadingHeight : 0)} zIndex={1000}>
            {loading && <LoadingBar height={loadingHeight} />}
            <Stack position="fixed" bottom="0" height={footerHeight} sx={{ backgroundColor: "#002C64", width: "100%" }} direction="row" justifyContent="center">
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
        </Stack>
    );
};
