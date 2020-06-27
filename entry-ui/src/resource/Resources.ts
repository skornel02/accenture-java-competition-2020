export type PermissionLevel = "WORKER" | "ADMIN" | "SUPER_ADMIN" | "DEVICE" | "INVALID";
export type RfidStatus = "UNKNOWN_RF_ID" | "OK" | "ERROR" | "FULL_HOUSE" | "NOT_INSIDE" | "NOT_OUTSIDE";

export interface AuthenticationInformation {
    permission: PermissionLevel,
}

export interface RfIdStatusResponse {
    status: RfidStatus,
    mapSvg?: string
}