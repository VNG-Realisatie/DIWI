import { Project } from "../api/projectsServices";
import { User } from "../api/userServices";
import { confidentialityLevelOptions } from "../components/table/constants";

export const checkIsOwnerValidWithConfidentialityLevel = (projectForm: Project, user: User | null): boolean => {
    if (projectForm) {
        if (projectForm?.projectOwners.length > 0) {
            if (projectForm?.confidentialityLevel) {
                const cL = confidentialityLevelOptions.find((cl) => cl.id === projectForm.confidentialityLevel);
                if (cL?.name === "PRIVATE") {
                    const isUserOwner = projectForm?.projectOwners.some((owner) => owner?.users?.some((u) => u.uuid === user?.id));
                    return isUserOwner;
                }
            }
        }
        return true;
    }
    return false;
};
