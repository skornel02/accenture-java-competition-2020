import React from 'react';
import loginStyles from './resource/style/login.module.css';
import {useForm} from "react-hook-form";
import logo from './resource/img/logo.png'
import GoogleLogin, {GoogleLoginResponse, GoogleLoginResponseOffline} from "react-google-login";
import {useTranslation} from "react-i18next";
import {toast} from "react-toastify";
import {BasicLoginInformation} from "./resource/Resources";

const Login: React.FunctionComponent<{
    handleBasicAuth: (data: BasicLoginInformation) => void,
    handleGoogleAuth: (token: string) => void,
}> = props => {
    const {register, handleSubmit, errors} = useForm<BasicLoginInformation>();
    const {t} = useTranslation();

    Object.keys(errors).forEach(keyStr => {
        const key = keyStr as keyof typeof errors;
        switch (key) {
            case "email":
                toast(t("login.emailMissing"), {toastId: "email", type: "error"})

                break;
            case "password":
                toast(t("login.passwordMissing"), {toastId: "password", type: "error"})

                break;
            default:
        }
        console.log(key, errors[key]);
    });

    const responseGoogle = (response: GoogleLoginResponse | GoogleLoginResponseOffline) => {
        if ("tokenId" in response) {
            props.handleGoogleAuth(response.tokenId);
        }
    }
    const password = t("login.password");
    const buttonText = t("login.button");

    return (
        <main className={loginStyles.LoginPage}>
            <form className={loginStyles.SignInForm}
                  onSubmit={handleSubmit(props.handleBasicAuth)}>
                <img className={loginStyles.SignInLogo} src={logo} alt="logo"/>
                <h1 style={{marginTop: "0", textAlign: "center"}}>{t("login.loginText")}</h1>
                <input className={loginStyles.SignInInput}
                       type="email"
                       placeholder="Email"
                       name="email"
                       ref={register({required: true})}/>
                <input className={loginStyles.SignInInput}
                       type="password"
                       placeholder={password}
                       name="password"
                       ref={register({required: true})}/>

                <input type="submit" className={loginStyles.SignInButton} value={buttonText}/>
                <hr/>
                <div style={{margin: "0 auto", width: 180}}>
                    <GoogleLogin onSuccess={responseGoogle}
                                 onFailure={e => toast("Google authentication failed...", {type: "error"})}
                                 clientId={"765993534694-p6c39vh4u187ld6v6gq11v5gnqal74b4.apps.googleusercontent.com"}/>
                </div>
            </form>
        </main>
    );
}

export default Login;