import React, { createContext, ReactNode } from "react";
import categoriesStore from "./GoalCategoriesStore";

const GoalCategoriesContext = createContext(categoriesStore);

interface CategoriesProviderProps {
    children: ReactNode;
}

export const CategoriesProvider = ({ children }: CategoriesProviderProps) => {
    return <GoalCategoriesContext.Provider value={categoriesStore}>{children}</GoalCategoriesContext.Provider>;
};

export default GoalCategoriesContext;
