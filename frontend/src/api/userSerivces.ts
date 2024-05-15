import { components } from "../types/schema";
import { getJson } from "../utils/requests";
import { API_URI } from "../utils/urls";

export type User = components["schemas"]["UserGroupUserModel"];

export async function getCurrentUser(): Promise<User> {
    return getJson(`${API_URI}/users/userinfo`);
}
