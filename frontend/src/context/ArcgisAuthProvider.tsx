import React, { createContext, useContext, useState, ReactNode } from "react";
import { ArcGISIdentityManager } from "@esri/arcgis-rest-request";

interface ArcgisAuthContextProps {
    login: (exportId: string) => void;
    handleRedirect: (exportId: string) => Promise<void>;
    token: string | null;
}

interface ArcgisAuthProviderProps {
    children: ReactNode;
}

export const ArcgisAuthContext = createContext<ArcgisAuthContextProps | undefined>(undefined);

export const ArcgisAuthProvider = ({ children }: ArcgisAuthProviderProps) => {
    const [token, setToken] = useState<string | null>(null);
    const [authManager, setAuthManager] = useState<ArcGISIdentityManager | null>(null);

    const clientId = "YOUR_CLIENT_ID"; // Move to .env

    const login = async (exportId: string) => {
        const redirectUri = `http://localhost:3000/exchangedata/export/${exportId}`;
        try {
            const manager = await ArcGISIdentityManager.beginOAuth2({
                clientId,
                redirectUri,
                popup: true,
            });

            if (manager) {
                setAuthManager(manager);
                setToken(manager.token);
            } else {
                console.error("Authentication failed");
            }
        } catch (error) {
            console.error("Authentication failed:", error);
        }
    };

    const handleRedirect = async (exportId: string) => {
        const redirectUri = `http://localhost:3000/exchangedata/export/${exportId}`;
        try {
            const manager = await ArcGISIdentityManager.completeOAuth2({
                clientId,
                redirectUri,
                popup: true,
            });
            setAuthManager(manager);
            setToken(manager.token);
        } catch (error) {
            console.error("Error completing OAuth2:", error);
        }
    };

    return <ArcgisAuthContext.Provider value={{ login, handleRedirect, token }}>{children}</ArcgisAuthContext.Provider>;
};
