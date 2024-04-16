import { Box, LinearProgress } from "@mui/material";
import useLoading from "../hooks/useLoading";

type LoadingProps = {
    height: number;
};

const LoadingBar = ({ height }: LoadingProps) => {
    const { loading } = useLoading();

    if (loading) {
        return (
            <Box
                width={"100%"}
                height={height}
                sx={{
                    zIndex: 10000,
                }}
            >
                <LinearProgress color="primary" variant="indeterminate" sx={{ height: height }} />
            </Box>
        );
    } else {
        return <></>;
    }
};

export default LoadingBar;
