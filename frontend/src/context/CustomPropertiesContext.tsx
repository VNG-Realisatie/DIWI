import { createContext } from "react";
import { CustomPropertyStoreType } from "../api/adminSettingServices";

export const CustomPropertyStoreContext = createContext<CustomPropertyStoreType | null>(null);
