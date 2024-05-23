import { useState, useEffect } from "react";
import { getCurrentUser } from "../api/userSerivces";

function useAllowedActions() {
    const [allowedActions, setAllowedActions] = useState<string[]>([]);

    useEffect(() => {
        getCurrentUser().then((user) => {
            if (user && user.allowedActions) setAllowedActions(user.allowedActions);
        });
    }, []);

    console.log(allowedActions);

    return allowedActions;
}

export default useAllowedActions;
