import React from 'react';
import SecuredArea from "./authentication/SecuredArea";
import {ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {I18nextProvider} from "react-i18next";
import i18n from "./resource/locale/i18n";
import MainComponent from "./component/MainComponent";

function App() {
  return (
    <div className="App">
      <I18nextProvider i18n={i18n}>
        <ToastContainer position={"top-center"}/>
        <SecuredArea>
            <MainComponent/>
        </SecuredArea>
      </I18nextProvider>
    </div>
  );
}

export default App;
