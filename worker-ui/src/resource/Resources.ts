export enum AuthorizationType {
    BASIC,
    GOOGLE
}

export interface Authentication {
    type: AuthorizationType
}

export interface BasicAuthentication extends Authentication {
    type: AuthorizationType.BASIC,
    username: string,
    password: string,
}

export interface GoogleAuthentication extends Authentication {
    type: AuthorizationType.GOOGLE,
    googleToken: string
}

export interface BasicLoginInformation {
    email: string,
    password: string
}