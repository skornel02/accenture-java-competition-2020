export enum AuthenticationType {
    NONE,
    BASIC,
    GOOGLE
}

export interface Authentication {
    type: AuthenticationType
}

export interface BasicAuthentication extends Authentication {
    type: AuthenticationType.BASIC,
    username: string,
    password: string,
}

export interface GoogleAuthentication extends Authentication {
    type: AuthenticationType.GOOGLE,
    googleToken: string
}

export interface BasicLoginInformation {
    email: string,
    password: string
}

export type PermissionLevel = "WORKER" | "ADMIN" | "SUPER_ADMIN" | "DEVICE" | "INVALID";

export type WorkerStatus = "WorkingFromHome" | "OnList" | "InOffice";

export interface WorkerResource {
    id: string,
    name: string,
    email: string,
    rfId: string,
    averageTime: number,
    status: WorkerStatus
}

export interface AdminResource {
    uuid: string,
    name: string,
    email: string,
}

export interface AuthenticationInformation {
    permission: PermissionLevel,
    worker?: WorkerResource,
    admin?: AdminResource
}

export interface LoginResource {
    id: number,
    arrivedAt: string,
    leftAt: string,
    worker: WorkerResource
}

export interface OfficeHoursResource {
    arrive: string,
    leave: string
}

export interface RemainingTime {
    projectedEntryTime: string,
    status: WorkerStatus
}

export interface TicketResource {
    targetDay: string,
    creationDate: string
}
