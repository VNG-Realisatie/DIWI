import { useContext } from "react";
import GoalCategoriesContext from "../context/GoalCategoriesContext";

export const useCategoriesStore = () => useContext(GoalCategoriesContext);
