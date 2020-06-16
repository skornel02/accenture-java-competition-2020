import React, {useState} from 'react';
import {Authentication, BasicLoginInformation} from "./resource/Resources";
import Loading from "./Loading";
import SessionManager from "./SessionManager";
import Login from "./Login";
import {toast} from "react-toastify";

const SecuredArea: React.FunctionComponent = props => {
    const [authorization, setAuthorization] = useState<Authentication | undefined>(SessionManager.getAuthentication());

    const handleBasicAuth = (auth: BasicLoginInformation) => {

    };

    const handleGoogleAuth = (auth: string) => {
        toast("Currently unsupported!", {type: "error"});
    };

    if (authorization == undefined)
        return (
            <Login handleBasicAuth={handleBasicAuth} handleGoogleAuth={handleGoogleAuth}/>
        );

    return (
        <>
            {props.children}
        </>
    );
};

export default SecuredArea;