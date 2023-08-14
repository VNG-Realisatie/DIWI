import { Alert } from "@mui/material";
import useAlert from "../hooks/useAlert";

const AlertPopup = () => {
    const { text, type, setAlert } = useAlert();

    if (text) {
        return (
            <Alert
                severity={type}
                variant="filled"
                onClose={() => {
                    setAlert("", "success");
                }}
                sx={{
                    position: "fixed",
                    bottom: 60,
                    right: 10,
                    zIndex: 10000,
                }}
            >
                {text}
            </Alert>
        );
    } else {
        return <></>;
    }
};

export default AlertPopup;
