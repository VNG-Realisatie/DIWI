import { createContext, useState, ReactNode, useEffect, useCallback } from "react";
import { ArcGISIdentityManager } from "@esri/arcgis-rest-request";
import { useNavigate } from "react-router-dom";
import { configuredExport } from "../Paths";
import useAlert from "../hooks/useAlert";
import { t } from "i18next";
import { getExportDataById } from "../api/exportServices";

interface ArcgisAuthContextProps {
    login: (exportId: string) => void;
    handleRedirect: () => Promise<void>;
    token: string | null;
}

interface ArcgisAuthProviderProps {
    children: ReactNode;
}

export const ArcgisAuthContext = createContext<ArcgisAuthContextProps | undefined>(undefined);

export const ArcgisAuthProvider = ({ children }: ArcgisAuthProviderProps) => {
    const [token, setToken] = useState<string | null>(null);
    const navigate = useNavigate();
    const { setAlert } = useAlert();
    const [exportId, setExportId] = useState<string | undefined>(undefined);
    const [clientId, setClientId] = useState<string | undefined>(undefined);
    const redirectUri = `${window.location.origin}/exchangeimportdata`;

    const login = async (exportId: string) => {
        setExportId(exportId);
        try {
            const { clientId } = await getExportDataById(exportId);
            setClientId(clientId);
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
        const manager = await ArcGISIdentityManager.completeOAuth2({
            clientId,
            redirectUri,
            popup: false,
        });
        setToken(manager.token);
        sessionStorage.setItem("arcgis_token", manager.token);
        setAlert(t("exchangeData.arcgis.loginSuccess"), "success");

        if (exportId) {
            navigate(configuredExport.toPath({ exportId }));
        }
    }, [clientId, redirectUri, navigate, setAlert, exportId]);

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
