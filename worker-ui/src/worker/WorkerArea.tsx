import React from 'react';
import {useAuthInformation} from "../authentication/SecuredArea";
import {RemainingTime} from "../resource/Resources";
import useSWR from "swr";
import Backend from "../resource/Backend";
import 'react-calendar/dist/Calendar.css';
import WorkerCountDown from "./WorkerCountDown";
import WorkerCalendar from "./WorkerCalendar";

const WorkerArea: React.FunctionComponent = () => {
    const info = useAuthInformation();
    const worker = info.worker!;
    const {data: remainingTime, revalidate: reValidate} = useSWR<RemainingTime>(
        () => "/workers/" + worker.id + "/remaining-time",
        () => Backend.getTimeRemaining(worker.id),
        {suspense: true});

    return (
        <div style={{display: "flex", flexDirection: "column"}}>
            {remainingTime !== undefined ? (
                <>
                    <WorkerCountDown worker={worker} remainingTime={remainingTime}/>
                    <hr/>
                    <WorkerCalendar worker={worker} remainingTime={remainingTime} reValidateRemainingTime={reValidate}/>
                </>
            ) : (<></>)}
        </div>
    )
};

export default WorkerArea;