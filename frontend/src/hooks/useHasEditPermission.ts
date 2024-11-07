import { useContext } from "react";
import ProjectContext from "../context/ProjectContext";
import UserContext from "../context/UserContext";

export const useHasEditPermission = () => {
    const { selectedProject } = useContext(ProjectContext);
    const { user, allowedActions } = useContext(UserContext);

    const isOwner = selectedProject?.projectOwners.some((owner) => owner.users?.some((u) => u.uuid === user?.id));

    const getEditPermission = () => {
        return (allowedActions.includes("EDIT_OWN_PROJECTS") && isOwner) || allowedActions.includes("EDIT_ALL_PROJECTS");
    };

    return { getEditPermission };
};
