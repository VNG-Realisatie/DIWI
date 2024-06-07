import { PropsWithChildren, createContext, useCallback, useEffect, useState } from "react";
import { User, getCurrentUser } from "../api/userSerivces";

type UserContextType = {
    user: User | null;
};

const UserContext = createContext<UserContextType | null>(null) as React.Context<UserContextType>;

export const UserProvider = ({ children }: PropsWithChildren) => {
    const [user, setUser] = useState<User | null>(null);

    const updateUser = useCallback(() => {
        getCurrentUser()
            .then((userResponse) => {
                setUser(userResponse);
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
            }}
        >
            {children}
        </UserContext.Provider>
    );
};
export default UserContext;
