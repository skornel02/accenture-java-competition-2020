import React, {useEffect, useState} from "react";
import {Detector} from 'react-detect-offline'
import {toast} from "react-toastify";

const OfflineToaster = () => {
    const [online, setOnline] = useState(true);

    useEffect(() => {
        if (!online) {
            toast("Offline", {
                toastId: "offline",
                type: "warning",
                autoClose: false,
                closeOnClick: false,
                closeButton: null,
                position: "bottom-center"
            })
        } else {
            toast.dismiss("offline")
        }
    });

    return (
        <Detector
            render={({ online }) => {
                setOnline(online);
                return <></>;
            }}
        />
    );
};

export default OfflineToaster;