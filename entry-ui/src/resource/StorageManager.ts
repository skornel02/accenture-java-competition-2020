import {Authentication} from "./Resources";
import localForage from 'localforage';

class StorageManager {
    async getAuthentication(): Promise<Authentication | null> {
        return await localForage.getItem<Authentication | null>("authentication");
    }

    async saveAuthentication(auth: Authentication): Promise<void> {
        await localForage.setItem("authentication", auth);
    }

    async clearAuthentication() {
        await localForage.removeItem("authentication");
    }
}

export default new StorageManager();