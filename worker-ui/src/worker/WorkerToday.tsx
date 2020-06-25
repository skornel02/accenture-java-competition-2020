import React from "react";
import WorkerCountDown from "./WorkerCountDown";
import {useAuthInformation} from "../authentication/SecuredArea";
import useSWR from "swr";
import {RemainingTime, WorkerStatus} from "../resource/Resources";
import Backend from "../resource/Backend";
import WorkerMap from "./WorkerMap";
import workerStyle from "../resource/style/worker.module.css";
// @ts-ignore
import {Online} from 'react-detect-offline'
import {useTranslation} from "react-i18next";
import moment from "moment";

const WorkerToday: React.FunctionComponent = () => {
    const info = useAuthInformation();
    const worker = info.worker!;
    const {data: remainingTime, revalidate: reValidate} = useSWR<RemainingTime>(
        () => "/workers/" + worker.id + "/remaining-time",
        () => Backend.getTimeRemaining(worker.id),
        {suspense: true});

    if (remainingTime === undefined)
        return null;

    return (
        <>
            <WorkerCountDown worker={worker} remainingTime={remainingTime}/>
            <ControlButton id={worker.id} status={remainingTime.status} reValidate={reValidate}/>
            <hr/>
            <WorkerMap svg={remainingTime.locationSVG}/>
        </>
    );
};

const ControlButton: React.FunctionComponent<{
    id: string,
    status: WorkerStatus,
    reValidate: () => void,
}> = props => {
    const {t} = useTranslation();

    const createTicket = () => {
        const date = moment().format("YYYY-MM-DD")
        Backend.createTicketForDay(props.id, date)
            .finally(() => {
                props.reValidate();
            });
    };

    const removeTicket = () => {
        const date = moment().format("YYYY-MM-DD")
        Backend.removeTicketFromDay(props.id, date)
            .finally(() => {
                props.reValidate();
            });
    }

    switch (props.status) {
        case "OnList":
            return (
                <>
                    <div className={workerStyle.ButtonWrapper}>
                        <Online>
                            <button className={workerStyle.DeleteButton} onClick={removeTicket}>
                                {t("worker.cancel")}
                            </button>
                        </Online>
                    </div>
                </>
            );
        case "WorkingFromHome":
            return (
                <>
                    <div className={workerStyle.ButtonWrapper}>
                        <Online>
                            <button className={workerStyle.CreateButton} onClick={createTicket}>
                                {t("worker.create")}
                            </button>
                        </Online>
                    </div>
                </>
            );
        default:
            return null;
    }
}

export default WorkerToday;