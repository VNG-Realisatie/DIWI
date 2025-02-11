import { createContext, useState, ReactNode, useEffect, useCallback } from "react";
import { ArcGISIdentityManager } from "@esri/arcgis-rest-request";

interface ArcgisAuthContextProps {
    login: () => void;
    handleRedirect: () => Promise<void>;
    token: string | null;
}

interface ArcgisAuthProviderProps {
    children: ReactNode;
}

export const ArcgisAuthContext = createContext<ArcgisAuthContextProps | undefined>(undefined);

export const ArcgisAuthProvider = ({ children }: ArcgisAuthProviderProps) => {
    const [token, setToken] = useState<string | null>(null);

    const clientId = import.meta.env.VITE_REACT_APP_ARCGIS_CLIENT_ID;
    const redirectUri = `${window.location.origin}/exchangeimportdata`;

    const login = async () => {
        try {
            await ArcGISIdentityManager.beginOAuth2({
                clientId,
                redirectUri,
                popup: false,
            });
        } catch (error) {
            console.error(error);
        }
    };

    const handleRedirect = useCallback(async () => {
        try {
            const manager = await ArcGISIdentityManager.completeOAuth2({
                clientId,
                redirectUri,
                popup: false,
            });
            setToken(manager.token);
            sessionStorage.setItem("arcgis_token", manager.token);
        } catch (error) {
            console.error(error);
        }
    }, [clientId, redirectUri]);

    useEffect(() => {
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.has("code")) {
            handleRedirect();
        } else {
            const storedToken = sessionStorage.getItem("arcgis_token");
            if (storedToken) {
                setToken(storedToken);
            }
        }
    }, [handleRedirect]);

    return <ArcgisAuthContext.Provider value={{ login, handleRedirect, token }}>{children}</ArcgisAuthContext.Provider>;
};
