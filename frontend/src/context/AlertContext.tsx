import { createContext, PropsWithChildren, useState } from "react";
import { AlertColor } from "@mui/material";

type AlertContextType = {
    text: string;
    type: AlertColor;
    setAlert(text: string, type: AlertColor): void;
};

const ALERT_TIME = 5000;

const AlertContext = createContext<AlertContextType>({
    text: "",
    type: "success",
    setAlert: (text: string, type: AlertColor) => {},
});

export const AlertProvider = ({ children }: PropsWithChildren) => {
    const [text, setText] = useState("");
    const [type, setType] = useState<AlertColor>("success");

    const setAlert = (text: string, type: AlertColor) => {
        setText(text);
        setType(type);

        setTimeout(() => {
            setText("");
            setType("success");
        }, ALERT_TIME);
    };

    return (
        <AlertContext.Provider
            value={{
                text,
                type,
                setAlert,
            }}
        >
            {children}
        </AlertContext.Provider>
    );
};

export default AlertContext;
