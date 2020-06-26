import React, {useEffect, useState} from 'react';
import StorageManager from "../resource/StorageManager";
import Login from "./Login";
import {toast} from "react-toastify";
import Backend from "../resource/Backend";
import Footer from "../general/Footer";
import {useTranslation} from "react-i18next";

const SecuredArea: React.FunctionComponent = props => {
    const [deviceToken, setDeviceToken] = useState<string | undefined>(undefined);
    const {t} = useTranslation();

    useEffect(() => {
        StorageManager.getDeviceToken()
            .then(token => {
                if (token !== null) {
                    setDeviceToken(token);
                }
            });
    }, []);

    const handleDeviceAuth = (auth: string) => {
        Backend.checkDeviceToken(auth).then(() => handleLogin(auth))
            .catch(() => {
                toast(t("login.invalidCredentials"), {type: "error"});
            })
    };

    const handleLogin = (token: string) => {
        Backend.updateAuthentication(token);
        setDeviceToken(token)
        StorageManager.saveDeviceToken(token).then(() => {});
    }

    const handleLogout = () => {
        setDeviceToken(undefined);
        StorageManager.clearDeviceToken().then(() => {});
    }

    if (deviceToken === undefined)
        return (
            <Login handleDeviceAuth={handleDeviceAuth}/>
        );

    return (
        <>
            <ContentArea>
                {props.children}
            </ContentArea>
            <Footer handleLogout={handleLogout}/>
        </>
    );
};

const ContentArea: React.FunctionComponent = props => {
    return (
        <div style={{minHeight: "96vh"}}>
            {props.children}
        </div>
    );
}

export default SecuredArea;