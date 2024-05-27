import { useState, useEffect } from "react";
import { getCurrentUser } from "../api/userSerivces";

export type AllowedActions =
    | "EDIT_CUSTOM_PROPERTIES"
    | "EDIT_USERS"
    | "CAN_OWN_PROJECTS"
    | "CHANGE_PROJECT_OWNER"
    | "CREATE_NEW_PROJECT"
    | "IMPORT_PROJECTS"
    | "EXPORT_PROJECTS"
    | "VIEW_OTHERS_PROJECTS";

const allowedActionsArray: AllowedActions[] = [
    "EDIT_CUSTOM_PROPERTIES",
    "EDIT_USERS",
    "CAN_OWN_PROJECTS",
    "CHANGE_PROJECT_OWNER",
    "CREATE_NEW_PROJECT",
    "IMPORT_PROJECTS",
    "EXPORT_PROJECTS",
    "VIEW_OTHERS_PROJECTS",
];

function useAllowedActions() {
    const [allowedActions, setAllowedActions] = useState<AllowedActions[]>([]);

    useEffect(() => {
        getCurrentUser().then((user) => {
            if (user) setAllowedActions(allowedActionsArray); //setAllowedActions(user.allowedActions)
        });
    }, []);

    return allowedActions;
}

export default useAllowedActions;
