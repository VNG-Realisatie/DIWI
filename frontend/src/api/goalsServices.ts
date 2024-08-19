import { deleteJson, getJson, postJson, putJson } from "../utils/requests";
import { API_URI } from "../utils/urls";

export type Category = {
    id: string;
    name: string;
};

export type GoalDirection = "MINIMAL" | "MAXIMAL" | "";
export type ConditionFieldType = "PROPERTY" | "GROUND_POSITION" | "PROGRAMMING" | "HOUSE_TYPE" | "OWNERSHIP" | "";

type Condition = {
    conditionId: string;
    conditionFieldType: ConditionFieldType;
    propertyId: string;
    propertyName: string;
    propertyKind: string;
    propertyType: string;
    booleanValue: boolean;
    categoryOptions: Category[];
    ordinalOptions: {
        value: number ;
        min: number;
        max: number;
    };
    listOptions: string[];
    ownershipOptions: {
        type: string;
        value: {
            value: number;
            min: number;
            max: number;
        };
        rangeCategoryOption: Category;
    }[];
};

type Geography = {
    conditionId: string;
    options: {
        brkGemeenteCode: string;
        brkSectie: string;
        brkPerceelNummer: number;
    }[];
};

export type Goal = {
    startDate: string | null | undefined;
    endDate: string | null | undefined;
    id: string;
    name: string;
    goalType: string;
    goalDirection: GoalDirection;
    goalValue: number;
    category: Category;
    conditions: Condition[];
    geography: Geography;
};

export const getAllGoals = async (): Promise<Goal[]> => {
    return getJson(`${API_URI}/goals`);
};

export const getAllCategories = async (): Promise<Category[]> => {
    return getJson(`${API_URI}/goals/categories`);
};

export const createGoal = async (goal: Goal): Promise<Goal> => {
    return postJson(`${API_URI}/goals`, goal);
};
