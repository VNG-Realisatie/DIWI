import { useState, useEffect } from "react";
import { getCurrentUser } from "../api/userServices";

export type AllowedActions =
    | "VIEW_API"
    | "VIEW_USERS" //
    | "EDIT_USERS" //
    | "VIEW_GROUPS" //
    | "EDIT_GROUPS" //
    | "VIEW_CONFIG"
    | "EDIT_CONFIG"
    | "VIEW_CUSTOM_PROPERTIES"
    | "EDIT_CUSTOM_PROPERTIES"
    | "CAN_OWN_PROJECTS"
    | "VIEW_OTHERS_PROJECTS"
    | "VIEW_OWN_PROJECTS"
    | "EDIT_OWN_PROJECTS"
    | "CREATE_NEW_PROJECT"
    | "IMPORT_PROJECTS" //
    | "EXPORT_PROJECTS"; //

function useAllowedActions() {
    const [allowedActions, setAllowedActions] = useState<AllowedActions[]>([]);
    console.log(allowedActions);

    useEffect(() => {
        getCurrentUser().then((user) => {
            if (user && user.allowedActions) setAllowedActions(user.allowedActions);
        });
    }, []);

    return allowedActions;
}

export default useAllowedActions;
