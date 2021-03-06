import React, {Suspense} from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import * as serviceWorker from './serviceWorker';
import App from "./App";
import Loading from "./general/Loading";
import {syncWithLocalStorage} from "swr-sync-storage";
syncWithLocalStorage();

ReactDOM.render(
  <React.StrictMode>
      <Suspense fallback={<Loading/>}>
          <App/>
      </Suspense>
  </React.StrictMode>,
  document.getElementById('root')
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.register();
