import axios, {AxiosError, AxiosInstance, AxiosRequestConfig} from 'axios/index';
import {
    Authentication,
    AuthenticationInformation,
    AuthenticationType,
    BasicAuthentication,
    GoogleAuthentication,
    OfficeHoursResource,
    RemainingTime,
    TicketResource
} from "./Resources";
import {Base64} from 'js-base64';

const settingsProduction = {
    url: "http://localhost:8080",
};

const settingsDevelopment = {
    url: "http://localhost:8080"
};

const ADDR: string = process.env.NODE_ENV === 'development' ? settingsDevelopment.url : settingsProduction.url;

const axiosBaseConfig: AxiosRequestConfig = {
    baseURL: ADDR,
    timeout: 8000,
    timeoutErrorMessage: "Server timeout",
    headers: {
        "Content-Type": "application/json"
    }
};

class Backend {
    private axios: AxiosInstance;

    constructor() {
        this.axios = axios.create({
            ...axiosBaseConfig
        });
    }

    updateAuthentication(auth: Authentication) {
        if (auth.type === AuthenticationType.BASIC) {
            const basicAuth = auth as BasicAuthentication;
            this.axios = axios.create({
                ...axiosBaseConfig,
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Basic " + Base64.encode(basicAuth.username + ":" + basicAuth.password),
                }
            });
        }
        if (auth.type === AuthenticationType.GOOGLE) {
            const googleAuth = auth as GoogleAuthentication;
            this.axios = axios.create({
                ...axiosBaseConfig,
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "GoogleToken " + googleAuth.googleToken,
                }
            });
        }
    }

    async checkBasicAuth(auth: BasicAuthentication): Promise<AuthenticationInformation> {
        try {
            const result = await this.axios.get<AuthenticationInformation>("/auth-information", {
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Basic " + Base64.encode(auth.username + ":" + auth.password),
                }
            });
            return result.data;
        } catch (err) {
            if (err) {
                const axiosError = err as AxiosError;
                if (err.response) {
                    console.error(axiosError.response);
                }
            }
            throw err;
        }
    }

    async checkGoogleAuth(auth: GoogleAuthentication): Promise<AuthenticationInformation> {
        try {
            const result = await this.axios.get<AuthenticationInformation>("/auth-information", {
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "GoogleToken " + auth.googleToken,
                }
            });
            return result.data;
        } catch (err) {
            if (err) {
                const axiosError = err as AxiosError;
                if (err.response) {
                    console.error(axiosError.response);
                }
            }
            throw err;
        }
    }

    async getAuthInformation(): Promise<AuthenticationInformation> {
        try {
            const result = await this.axios.get<AuthenticationInformation>("/auth-information");
            return result.data;
        } catch (err) {
            if (err) {
                const axiosError = err as AxiosError;
                if (err.response) {
                    console.error(axiosError.response);
                }
            }
            throw err;
        }
    }

    async getTimeRemaining(userId: string): Promise<RemainingTime> {
        try {
            const result = await this.axios.get<RemainingTime>("/users/" + userId + "/entry-time-remaining");
            return result.data;
        } catch (err) {
            if (err) {
                const axiosError = err as AxiosError;
                if (err.response) {
                    console.error(axiosError.response);
                }
            }
            throw err;
        }
    }

    async getTickets(userId: string): Promise<TicketResource[]> {
        try {
            const result = await this.axios.get<TicketResource[]>("/users/" + userId + "/tickets");
            return result.data;
        } catch (err) {
            if (err) {
                const axiosError = err as AxiosError;
                if (err.response) {
                    console.error(axiosError.response);
                }
            }
            throw err;
        }
    }

    async getOfficeHours(userId: string): Promise<OfficeHoursResource[]> {
        try {
            const result = await this.axios.get<OfficeHoursResource[]>("/users/" + userId + "/office-hours");
            return result.data;
        } catch (err) {
            if (err) {
                const axiosError = err as AxiosError;
                if (err.response) {
                    console.error(axiosError.response);
                }
            }
            throw err;
        }
    }

    async createTicketForDay(userId: string, date: string): Promise<void> {
        try {
            const result = await this.axios.put<void>("/users/" + userId + "/tickets/" + date);
            return result.data;
        } catch (err) {
            if (err) {
                const axiosError = err as AxiosError;
                if (err.response) {
                    console.error(axiosError.response);
                }
            }
            throw err;
        }
    }

    async removeTicketFromDay(userId: string, date: string): Promise<void> {
        try {
            const result = await this.axios.delete<void>("/users/" + userId + "/tickets/" + date);
            return result.data;
        } catch (err) {
            if (err) {
                const axiosError = err as AxiosError;
                if (err.response) {
                    console.error(axiosError.response);
                }
            }
            throw err;
        }
    }

}

export default new Backend();