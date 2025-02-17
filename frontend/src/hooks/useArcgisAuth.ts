import { useContext } from "react";
import { ArcgisAuthContext } from "../context/ArcgisAuthProvider";

export const useArcgisAuth = () => {
    const context = useContext(ArcgisAuthContext);
    if (!context) {
        throw new Error("useArcgisAuth must be used within a ArcgisAuthProvider");
    }
    return {
        ...context,
        login: (exportId : string) => context.login(exportId),
        handleRedirect: () => context.handleRedirect(),
    };
};
