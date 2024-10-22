import { Group } from "../pages/UserManagement";
import { RoleType } from "../types/enums";
import { components } from "../types/schema";
import { getJson, postJson, deleteJson, putJson } from "../utils/requests";
import { API_URI } from "../utils/urls";

export type User = components["schemas"]["UserGroupUserModel"] & {
    role?: RoleType | undefined | string;
    organization?: string;
    phoneNumber?: string;
    department?: string;
    contactPerson?: string;
    prefixes?: string;
    allowedActions?: AllowedActions[];
};

export type AllowedActions =
    | "VIEW_API"
    | "VIEW_USERS"
    | "EDIT_USERS"
    | "VIEW_GROUPS"
    | "EDIT_GROUPS"
    | "VIEW_CONFIG"
    | "EDIT_CONFIG"
    | "VIEW_CUSTOM_PROPERTIES"
    | "EDIT_CUSTOM_PROPERTIES"
    | "CAN_OWN_PROJECTS"
    | "VIEW_OTHERS_PROJECTS"
    | "VIEW_OWN_PROJECTS"
    | "EDIT_OWN_PROJECTS"
    | "EDIT_ALL_PROJECTS"
    | "CREATE_NEW_PROJECT"
    | "IMPORT_PROJECTS"
    | "EXPORT_PROJECTS"
    | "VIEW_ALL_BLUEPRINTS"
    | "EDIT_ALL_BLUEPRINTS"
    | "VIEW_OWN_BLUEPRINTS"
    | "VIEW_DASHBOARDS"
    | "EDIT_GOALS"
    | "VIEW_GOALS";

export async function getCurrentUser(): Promise<User> {
    return getJson(`${API_URI}/users/userinfo`);
}

export async function getUsers(): Promise<User[]> {
    return getJson(`${API_URI}/users`);
}

export async function addUser(data: User): Promise<User> {
    return postJson(`${API_URI}/users`, data);
}
export async function deleteUser(id: string): Promise<void> {
    return deleteJson(`${API_URI}/users/${id}`);
}

export async function updateUser(id: string, data: User): Promise<User> {
    return putJson(`${API_URI}/users/${id}`, data);
}

export async function getGroups(): Promise<Group[]> {
    return getJson(`${API_URI}/groups`);
}

export async function addGroup(data: Group): Promise<Group> {
    return postJson(`${API_URI}/groups`, data);
}

export async function deleteGroup(id: string): Promise<void> {
    return deleteJson(`${API_URI}/groups/${id}`);
}

export async function updateGroup(id: string, data: Group): Promise<Group> {
    return putJson(`${API_URI}/groups/${id}`, data);
}
