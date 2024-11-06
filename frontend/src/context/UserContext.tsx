import { PropsWithChildren, createContext, useCallback, useEffect, useState } from "react";
import { AllowedActions, User, getCurrentUser } from "../api/userServices";

type UserContextType = {
    user: User | null;
    allowedActions: AllowedActions[];
};

const UserContext = createContext<UserContextType | null>(null) as React.Context<UserContextType>;

export const UserProvider = ({ children }: PropsWithChildren) => {
    const [user, setUser] = useState<User | null>(null);
    const [allowedActions, setAllowedActions] = useState<AllowedActions[]>([]);

    const updateUser = useCallback(() => {
        getCurrentUser()
            .then((userResponse) => {
                setUser(userResponse);
                if (userResponse && userResponse.allowedActions) {
                    setAllowedActions(userResponse.allowedActions);
                }
            })
            .catch((e) => {
                console.log(e);
            });
    }, []);

    useEffect(() => {
        updateUser();
    }, [updateUser]);

    return (
        <UserContext.Provider
            value={{
                user,
                allowedActions,
            }}
        >
            {children}
        </UserContext.Provider>
    );
};
export default UserContext;
