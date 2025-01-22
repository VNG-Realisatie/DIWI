import { ReactNode } from "react";
import { customPropertyStore } from "../stores/CustomPropertiesStore";
import { CustomPropertyStoreContext } from "./CustomPropertiesContext";


export const CustomPropertyStoreProvider = ({ children }: { children: ReactNode }) => {
    return <CustomPropertyStoreContext.Provider value={customPropertyStore}>{children}</CustomPropertyStoreContext.Provider>;
};
