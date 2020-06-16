import axios, {AxiosInstance, AxiosRequestConfig} from 'axios/index';

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


}

export default new Backend();