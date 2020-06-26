import axios, {AxiosError, AxiosInstance, AxiosRequestConfig} from 'axios/index';
import {AuthenticationInformation, RfIdStatusResponse} from "./Resources";

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

    updateAuthentication(deviceToken: string) {
        this.axios = axios.create({
            ...axiosBaseConfig,
            headers: {
                "Content-Type": "application/json",
                "Authorization": "DeviceToken " + deviceToken,
            }
        });
    }

    async checkDeviceToken(deviceToken: string): Promise<AuthenticationInformation> {
        try {
            const result = await this.axios.get<AuthenticationInformation>("/auth-information", {
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "DeviceToken " + deviceToken,
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

    async checkIn(rfId: string): Promise<RfIdStatusResponse> {
        try {
            const result = await this.axios.post<RfIdStatusResponse>(`/rfids/${rfId}/checkin`);
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

    async checkOut(rfId: string): Promise<RfIdStatusResponse> {
        try {
            const result = await this.axios.post<RfIdStatusResponse>(`/rfids/${rfId}/checkout`);
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