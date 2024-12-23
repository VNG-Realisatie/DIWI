import React, { createContext, useContext, useState, ReactNode, useRef } from "react";
import { generateCodeChallenge, generateCodeVerifier } from "../utils/pkceUtils";

interface ArcgisAuthContextProps {
    login: () => void;
    handleRedirect: () => Promise<void>;
    token: string | null;
}

interface ArcgisAuthProviderProps {
    children: ReactNode;
}

const ArcgisAuthContext = createContext<ArcgisAuthContextProps | undefined>(undefined);

export const ArcgisAuthProvider = ({ children }: ArcgisAuthProviderProps) => {
    const [token, setToken] = useState<string | null>(null);
    const codeVerifier = useRef<string>("");
    const auth_url = "https://www.arcgis.com/sharing/rest/oauth2/authorize";
    const token_url = "https://www.arcgis.com/sharing/rest/oauth2/token";
    const client_id = "YOUR_CLIENT_ID"; // move to .env
    const redirect_uri = "http://localhost:3000/exchangedata/export/0193ba92-b3cd-78da-a994-739c0108e685"; //updare redirect uri

    const login = async () => {
        codeVerifier.current = generateCodeVerifier();
        const codeChallenge = await generateCodeChallenge(codeVerifier.current);

        const authUrl = `${auth_url}?client_id=${client_id}&response_type=code&redirect_uri=${encodeURIComponent(
            redirect_uri!,
        )}&code_challenge=${codeChallenge}&code_challenge_method=S256`;

        window.location.href = authUrl;
    };

    const handleRedirect = async () => {
        const params = new URLSearchParams(window.location.search);
        const code = params.get("code");

        if (code) {
            const response = await fetch(token_url!, {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
                body: new URLSearchParams({
                    client_id: client_id!,
                    grant_type: "authorization_code",
                    code,
                    redirect_uri: redirect_uri!,
                    code_verifier: codeVerifier.current,
                }).toString(),
            });

            const data = await response.json();
            setToken(data.access_token);
        }
    };

    return <ArcgisAuthContext.Provider value={{ login, handleRedirect, token }}>{children}</ArcgisAuthContext.Provider>;
};

export const useArcgisAuth = () => {
    const context = useContext(ArcgisAuthContext);
    return context;
};
