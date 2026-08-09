import axios from "axios";
import keycloak from "../auth/keycloak";


const api = axios.create({
    baseURL: "http://localhost:8081"
});


api.interceptors.request.use(
    async (config) => {
        try {
            if (!keycloak.authenticated) {
                return config;
            }
            await keycloak.updateToken(30);

            config.headers.Authorization =
                `Bearer ${keycloak.token}`;

            return config;

        } catch (error) {
            console.error(
                "Token refresh failed:",
                error
            );
            return Promise.reject(error);
        }
    },

    error => Promise.reject(error)
);


export default api;