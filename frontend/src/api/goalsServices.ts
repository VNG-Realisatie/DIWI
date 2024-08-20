import { deleteJson, getJson, postJson, putJson } from "../utils/requests";
import { API_URI } from "../utils/urls";

export type Category = {
    id: string;
    name: string;
};

export type GoalDirection = "MINIMAL" | "MAXIMAL" | "";
export type ConditionFieldType = "PROPERTY" | "GROUND_POSITION" | "PROGRAMMING" | "HOUSE_TYPE" | "OWNERSHIP" | "";
export type PropertyKind = "FIXED" | "CUSTOM";
export type PropertyType = "BOOLEAN" | "CATEGORY" | "ORDINAL" | "NUMERIC" | "TEXT" | "RANGE_CATEGORY";


type Condition = {
    conditionId: string;
    conditionFieldType: ConditionFieldType;
    propertyId: string;
    propertyName: string;
    propertyKind: PropertyKind;
    propertyType: PropertyType;
    booleanValue: boolean;
    categoryOptions: Category[];
    ordinalOptions: OrdinalOptions;
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

export type OrdinalOption = {
    id: string;
    name: string;
};

export type OrdinalOptions = {
    value: OrdinalOption;
    min: OrdinalOption;
    max: OrdinalOption;
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

export const getGoal = async (goalId: string): Promise<Goal> => {
    return getJson(`${API_URI}/goals/${goalId}`);
}

export const getAllCategories = async (): Promise<Category[]> => {
    return getJson(`${API_URI}/goals/categories`);
};

export const createGoal = async (goal: Goal): Promise<Goal> => {
    return postJson(`${API_URI}/goals`, goal);
};

export const updateGoal = async (goal: Goal): Promise<Goal> => {
    return putJson(`${API_URI}/goals/${goal.id}`, goal);
};

export const deleteGoal = async (goalId: string): Promise<void> => {
    return deleteJson(`${API_URI}/goals/${goalId}`);
}


