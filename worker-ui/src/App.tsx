import React from 'react';
import './App.css';
import {I18nextProvider} from "react-i18next";
import i18n from "./resource/locale/i18n";
import {ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import SecuredArea, {useAuthInformation} from "./SecuredArea";
import WorkerArea from "./WorkerArea";
import AdminArea from "./AdminArea";

const App = () => {
    return (
        <I18nextProvider i18n={i18n}>
            <ToastContainer position={"top-center"}/>
            <SecuredArea>
                <AreaChooser/>
            </SecuredArea>
        </I18nextProvider>
    );
}

const AreaChooser = () => {
    const info = useAuthInformation();
    switch (info.permission) {
        case "SUPER_ADMIN":
        case "ADMIN":
            return <AdminArea/>;
        case "WORKER":
            return <WorkerArea/>;
        default:
            return <h1>Not supported!</h1>
    }
};

export default App;
