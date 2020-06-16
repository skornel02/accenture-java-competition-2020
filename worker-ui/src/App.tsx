import React from 'react';
import './App.css';
import {I18nextProvider} from "react-i18next";
import i18n from "./resource/locale/i18n";
import {ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import SecuredArea from "./SecuredArea";

function App() {
    return (
        <I18nextProvider i18n={i18n}>
            <ToastContainer position={"top-center"}/>
            <SecuredArea>

            </SecuredArea>
        </I18nextProvider>
    );
}

export default App;
