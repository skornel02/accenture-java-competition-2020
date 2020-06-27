import localForage from 'localforage';

class StorageManager {
    async getDeviceToken(): Promise<string | null> {
        return await localForage.getItem<string | null>("deviceToken");
    }

    async saveDeviceToken(deviceToken: string): Promise<void> {
        await localForage.setItem("deviceToken", deviceToken);
    }

    async clearDeviceToken() {
        await localForage.removeItem("deviceToken");
    }
}

export default new StorageManager();