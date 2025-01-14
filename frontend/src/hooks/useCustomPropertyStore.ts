import { useContext } from "react";
import { CustomPropertyStoreContext } from "../context/CustomPropertiesContext";

export const useCustomPropertyStore = () => {
    const context = useContext(CustomPropertyStoreContext);
    if (!context) {
        throw new Error("useCustomPropertyStore must be used within a CustomPropertyStoreProvider");
    }
    return context;
};
