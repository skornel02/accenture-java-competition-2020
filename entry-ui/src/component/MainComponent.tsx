import React, {useEffect, useState} from "react";
import style from '../resource/style/main.module.css';
import SvgDisplay from './SvgDisplay';
import {useTranslation} from "react-i18next";
import Backend from "../resource/Backend";
import {toast} from "react-toastify";

const MainComponent: React.FunctionComponent = () => {
    const [mapSvg, setMapSvg] = useState<string | undefined>(undefined);
    const [rfid, setRfid] = useState<string>("");
    const {t} = useTranslation();
    useEffect(() => {
        let timeout: NodeJS.Timeout | undefined = undefined;
        if (mapSvg !== undefined) {
            timeout = setTimeout(() => {
                setMapSvg(undefined);
            }, 8000);
        }
        return () => {
            if (timeout !== undefined)
                clearTimeout(timeout);
        }
    })

    const handleEntry = () => {
        if (rfid.length === 0)
            return;
        Backend.checkIn(rfid)
            .then(result => {
                switch (result.status) {
                    case "ERROR":
                        toast("Unrecognized error", {type: "error"});
                        break;
                    case "FULL_HOUSE":
                        toast(t("rfid.fullHouse"), {type: "error"});
                        break;
                    case "UNKNOWN_RF_ID":
                        toast(t("rfid.rfidNotFound"), {type: "error"});
                        break;
                    case "NOT_OUTSIDE":
                        toast(t("rfid.alreadyInside"), {type: "info"});
                        break;
                    case "OK":
                        toast(t("rfid.youCanEnter"), {type: "success"})
                        setMapSvg(result.mapSvg);
                        break;
                }
            })
            .catch(err => {
                toast("Unrecognized error", {type: "error"});
                console.log(err.response);
            });
    };

    const handleLeave = () => {
        if (rfid.length === 0)
            return;
        Backend.checkOut(rfid)
            .then(result => {
                switch (result.status) {
                    case "ERROR":
                        toast("Unrecognized error", {type: "error"});
                        break;
                    case "FULL_HOUSE":
                        toast(t("rfid.fullHouse"), {type: "error"});
                        break;
                    case "UNKNOWN_RF_ID":
                        toast(t("rfid.rfidNotFound"), {type: "error"});
                        break;
                    case "NOT_INSIDE":
                        toast(t("rfid.alreadyOutside"), {type: "info"});
                        break;
                    case "OK":
                        toast(t("rfid.youCanLeave"), {type: "success"})
                        break;
                }
            })
            .catch(err => {
                toast("Unrecognized error", {type: "error"});
                console.log(err.response);
            });
    };

    const rfidPlaceholder = t("rfid.rfid");
    const entryButtonText = t("rfid.enter");
    const leaveButtonText = t("rfid.leave");

    return (<div className={style.EntryWrapper}>
        <SvgDisplay svg={mapSvg}/>
        <form className={style.InputForm}
              onSubmit={e => {
                  e.preventDefault();
              }}>
            <input className={style.Input}
                   type="text"
                   placeholder={rfidPlaceholder}
                   name="rfid"
                   onChange={e => setRfid(e.target.value)}
                   value={rfid}/>
            <input type="submit" className={style.EntryButton} value={entryButtonText} onClick={handleEntry}/>
            <input type="submit" className={style.LeaveButton} value={leaveButtonText} onClick={handleLeave}/>
        </form>
    </div>);
}

export default MainComponent;