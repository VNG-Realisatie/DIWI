import { useState, useEffect } from "react";
import { getCurrentUser } from "../api/userServices";

function useCurrentUserRole() {
    const [currentUserRole, setCurrentUserRole] = useState<string | undefined>(undefined);
    useEffect(() => {
        getCurrentUser().then((user) => {
            if (user) {
                setCurrentUserRole(user.role);
            }
        });
    }, []);

    return { currentUserRole };
}

export default useCurrentUserRole;
