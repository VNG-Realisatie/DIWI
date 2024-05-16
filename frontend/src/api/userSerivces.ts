import { components } from "../types/schema";
import { getJson, postJson, deleteJson, putJson } from "../utils/requests";
import { API_URI } from "../utils/urls";

export type User = components["schemas"]["UserGroupUserModel"];

export async function getCurrentUser(): Promise<User> {
    return getJson(`${API_URI}/users/userinfo`);
}

export async function getUsers(): Promise<User[]> {
    return getJson(`${API_URI}/users`);
}

export async function getGroups(): Promise<any> {
    return getJson(`${API_URI}/groups`);
}

export async function addGroup(data: any): Promise<any> {
    return postJson(`${API_URI}/groups`, data);
}

export async function deleteGroup(id: string): Promise<any> {
    return deleteJson(`${API_URI}/groups/${id}`);
}

export async function updateGroup(id: string, data: any): Promise<any> {
    return putJson(`${API_URI}/groups/${id}`, data);
}
