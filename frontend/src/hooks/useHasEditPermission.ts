import { useContext } from "react";
import ProjectContext from "../context/ProjectContext";
import UserContext from "../context/UserContext";
import useAllowedActions from "./useAllowedActions";

export const useHasEditPermission = () => {
    const { selectedProject } = useContext(ProjectContext);
    const { user } = useContext(UserContext);

    const { allowedActions } = useAllowedActions();

    const isOwner = selectedProject?.projectOwners.some((owner) => owner.users?.some((u) => u.uuid === user?.id));

    const getEditPermission = () => {
        return (allowedActions.includes("EDIT_OWN_PROJECTS") && isOwner) || allowedActions.includes("EDIT_ALL_PROJECTS");
    };

    return { getEditPermission };
};
