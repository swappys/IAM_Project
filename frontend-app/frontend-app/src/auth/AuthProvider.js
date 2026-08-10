import { createContext, useEffect, useState } from "react";
import keycloak from "./keycloak";

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {

    const [authenticated, setAuthenticated] = useState(false);
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {

        const initKeycloak = async () => {

            try {

                const auth = await keycloak.init({
                    onLoad: "check-sso",
                    pkceMethod: "S256"
                });

                if (!auth) {

                    console.log("No Keycloak session found, redirecting to login");

                    keycloak.login({
                        acr: {
                            values: ["otp"],
                            essential: false
                        }
                    });

                    return;
                }
                console.log("ACR:", keycloak.tokenParsed?.acr);
                setAuthenticated(true);

                setUser({
                    username:
                        keycloak.tokenParsed?.preferred_username,

                    token:
                        keycloak.token,

                    roles:
                        keycloak.tokenParsed?.realm_access?.roles || []
                });

            } catch (error) {

                console.error(
                    "Keycloak initialization failed:",
                    error
                );

            } finally {

                setLoading(false);
            }
        };

        initKeycloak();

    }, []);

    if (loading) {
        return <div>Loading...</div>;
    }

    return (
        <AuthContext.Provider
            value={{
                authenticated,
                user,
                keycloak
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}