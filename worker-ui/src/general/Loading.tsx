import React from 'react';
import loadingGif from '../resource/img/loading-compressed.gif';

const Loading: React.FunctionComponent = () => {
    return (<div style={{
            position: "fixed",
            zIndex: 1000,
            width: "100%",
            height: "100%",
            top: 0,
            left: 0,
            bottom: 0,
            right: 0,
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            textAlign: "center",
            background: "rgba(0,0,0,0.3)"
        }}>
            <img src={loadingGif} alt={"Loading..."} style={{margin: "0 auto", width: "280px"}}/>
        </div>
    );
};

export default Loading;