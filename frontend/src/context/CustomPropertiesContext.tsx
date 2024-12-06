import React, { createContext, useContext } from "react";
import { customPropertyStore } from "../stores/CustomPropertiesStore";

const CustomPropertyStoreContext = createContext<CustomPropertyStoreType | null>(null);

import { ReactNode } from "react";
import { CustomPropertyStoreType } from "../api/adminSettingServices";

export const CustomPropertyStoreProvider = ({ children }: { children: ReactNode }) => {
    return <CustomPropertyStoreContext.Provider value={customPropertyStore}>{children}</CustomPropertyStoreContext.Provider>;
};

export const useCustomPropertyStore = () => {
    const context = useContext(CustomPropertyStoreContext);
    if (!context) {
        throw new Error("useCustomPropertyStore must be used within a CustomPropertyStoreProvider");
    }
    return context;
};
