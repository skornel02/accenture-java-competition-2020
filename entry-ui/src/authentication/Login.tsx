import React from 'react';
import loginStyles from '../resource/style/login.module.css';
import {useForm} from "react-hook-form";
import logo from '../resource/img/logo.png'
import {useTranslation} from "react-i18next";
import {toast} from "react-toastify";

interface LoginForm {
    deviceToken: string
}

const Login: React.FunctionComponent<{
    handleDeviceAuth: (token: string) => void,
}> = props => {
    const {register, handleSubmit, errors} = useForm<LoginForm>();
    const {t} = useTranslation();

    Object.keys(errors).forEach(keyStr => {
        const key = keyStr as keyof typeof errors;
        if (key === "deviceToken") {
            toast(t("login.deviceTokenMissing"), {toastId: "deviceToken", type: "error"})
        }
        console.log(key, errors[key]);
    });

    const deviceToken = t("login.deviceToken");
    const buttonText = t("login.button");

    return (
        <main className={loginStyles.LoginPage}>
            <form className={loginStyles.SignInForm}
                  onSubmit={handleSubmit(data => props.handleDeviceAuth(data.deviceToken))}>
                <img className={loginStyles.SignInLogo} src={logo} alt="logo"/>
                <h1 style={{marginTop: "0", textAlign: "center"}}>{t("login.loginText")}</h1>
                <input className={loginStyles.SignInInput}
                       type="text"
                       placeholder={deviceToken}
                       name="deviceToken"
                       ref={register({required: true, minLength: 1})}/>
                <input type="submit" className={loginStyles.SignInButton} value={buttonText}/>
            </form>
        </main>
    );
}

export default Login;