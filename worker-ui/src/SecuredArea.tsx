import React, {useEffect, useState} from 'react';
import {
    Authentication,
    AuthenticationInformation,
    AuthenticationType,
    BasicAuthentication,
    BasicLoginInformation, GoogleAuthentication
} from "./resource/Resources";
import StorageManager from "./StorageManager";
import Login from "./Login";
import {toast} from "react-toastify";
import Backend from "./resource/Backend";
import Footer from "./Footer";
import {useTranslation} from "react-i18next";

const AuthContext = React.createContext<AuthenticationInformation>({
    permission: "INVALID",
    admin: undefined,
    worker: undefined
});

const SecuredArea: React.FunctionComponent = props => {
    const [authentication, setAuthentication] = useState<Authentication | undefined>(undefined);
    const [authInformation, setAuthInformation] = useState<AuthenticationInformation | undefined>(undefined);
    const {t} = useTranslation();

    useEffect(() => {
        StorageManager.getAuthentication()
            .then(result => {
                if (result !== null) {
                    const narrow: Authentication = result;
                    Backend.updateAuthentication(narrow);
                    Backend.getAuthInformation().then(setAuthInformation)
                    setAuthentication(narrow);
                }
            });
    }, []);

    const handleBasicAuth = (auth: BasicLoginInformation) => {
        const basicAuth: BasicAuthentication = {
            type: AuthenticationType.BASIC,
            username: auth.email,
            password: auth.password
        }
        Backend.checkBasicAuth(basicAuth).then(authInfo => handleLogin(basicAuth, authInfo))
            .catch(() => {
                toast(t("login.invalidCredentials"), {type: "error"});
            })
    };

    const handleGoogleAuth = (auth: string) => {
        const googleAuth: GoogleAuthentication = {
            type: AuthenticationType.GOOGLE,
            googleToken: auth,
        }
        Backend.checkGoogleAuth(googleAuth).then(authInfo => handleLogin(googleAuth, authInfo))
            .catch(() => {
                toast(t("login.invalidToken"), {type: "error"});
            })
    };

    const handleLogin = (auth: Authentication, authInfo: AuthenticationInformation) => {
        Backend.updateAuthentication(auth);
        setAuthInformation(authInfo);
        setAuthentication(auth)
        StorageManager.saveAuthentication(auth)
            .then(() => {
                if (authInfo.permission !== "WORKER") {
                    toast(t("login.onlyWorkers"), {type: "error"});
                    handleLogout();
                }
            })
    }

    const handleLogout = () => {
        setAuthentication(undefined);
        setAuthInformation(undefined);
        StorageManager.clearAuthentication();
    }

    if (authentication === undefined || authInformation === undefined)
        return (
            <Login handleBasicAuth={handleBasicAuth} handleGoogleAuth={handleGoogleAuth}/>
        );

    return (
        <AuthContext.Provider value={authInformation}>
            <ContentArea>
                {props.children}
            </ContentArea>
            <Footer handleLogout={handleLogout}/>
        </AuthContext.Provider>
    );
};

const ContentArea: React.FunctionComponent = props => {
    return (
        <div style={{minHeight: "96vh"}}>
            {props.children}
        </div>
    );
}

function useAuthInformation(): AuthenticationInformation {
    const ctx = React.useContext<AuthenticationInformation>(AuthContext);
    if (ctx === undefined) {
        throw new Error('useAuthInformation must be used within a AuthContextProvider')
    }
    return ctx;
}

export default SecuredArea;
export {useAuthInformation}