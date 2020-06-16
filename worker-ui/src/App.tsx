import React from 'react';
import {I18nextProvider} from "react-i18next";
import i18n from "./resource/locale/i18n";
import {ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import SecuredArea from "./authentication/SecuredArea";
import WorkerArea from "./worker/WorkerArea";
import OfflineToaster from "./general/OfflineToaster";

const App = () => {
    return (
        <I18nextProvider i18n={i18n}>
            <ToastContainer position={"top-center"}/>
            <OfflineToaster/>
            <SecuredArea>
                <WorkerArea/>
            </SecuredArea>
        </I18nextProvider>
    );
}


export default App;
