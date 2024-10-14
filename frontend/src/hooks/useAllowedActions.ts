import { useState, useEffect } from "react";
import { AllowedActions, getCurrentUser } from "../api/userServices";

function useAllowedActions() {
    const [allowedActions, setAllowedActions] = useState<AllowedActions[]>([]);
    useEffect(() => {
        getCurrentUser().then((user) => {
            if (user && user.allowedActions) {
                setAllowedActions(user.allowedActions);
            }
        });
    }, []);

    return { allowedActions };
}

export default useAllowedActions;
