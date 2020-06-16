import React, {useState} from 'react';
import {useAuthInformation} from "./SecuredArea";
import {OfficeHoursResource, RemainingTime, TicketResource, WorkerResource} from "./resource/Resources";
import useSWR from "swr";
import Backend from "./resource/Backend";
import {useTranslation} from "react-i18next";
import workerStyle from './resource/style/worker.module.css';
import Calendar from 'react-calendar';
import {firstDayInPreviousMonth, firstDayInTwoMonths, yesterday} from "./DateUtils";
import 'react-calendar/dist/Calendar.css';
import moment from "moment";
// @ts-ignore
import {Online} from 'react-detect-offline'

const WorkerArea: React.FunctionComponent = props => {
    const info = useAuthInformation();
    const worker = info.worker!;
    const {data: remainingTime, error: remainingTimeError, revalidate} = useSWR<RemainingTime>(
        () => "/workers/" + worker.id + "/remaining-time",
        () => Backend.getTimeRemaining(worker.id),
        {suspense: true});

    return (
        <div style={{display: "flex", flexDirection: "column"}}>
            <CountDown worker={worker} remainingTime={remainingTime}/>
            <hr/>
            <WorkerCalendar worker={worker} remainingTime={remainingTime} revalidateRemainingTime={revalidate}/>
        </div>
    )
};

const CountDown: React.FunctionComponent<{
    worker: WorkerResource,
    remainingTime: RemainingTime | undefined,
}> = props => {
    const {t} = useTranslation();

    const projectedTime = props.remainingTime?.projectedEntryTime === "00:00:00" ?
        t("worker.instantly")
        : props.remainingTime?.projectedEntryTime;

    switch (props.remainingTime?.status) {
        case "InOffice":
            return (
                <div>
                    <h1 className={workerStyle.BigText}>
                        {t("worker.currentlyInside")}
                    </h1>
                </div>
            );
        case "OnList":
            return (
                <div>
                    <h1 className={workerStyle.BigText}>
                        {t("worker.currentlyWaiting")}
                    </h1>
                    <h2 className={workerStyle.SmallText}>
                        {t("worker.estimatedTimeToEntry")}
                    </h2>
                    <h3 className={workerStyle.TimeText}>
                        {projectedTime}
                    </h3>
                </div>
            );
        case "WorkingFromHome":
            return (
                <div>
                    <h1 className={workerStyle.BigText}>
                        {t("worker.currentlyNotWaiting")}
                    </h1>
                    <h2 className={workerStyle.SmallText}>
                        {t("worker.estimatedTimeToEntry")}
                        <span className={workerStyle.HintText}>
                            ({t("worker.entryMightChange")})
                        </span>
                    </h2>
                    <h3 className={workerStyle.TimeText}>
                        {projectedTime}
                    </h3>
                </div>
            );
    }
    return null;
}

const WorkerCalendar: React.FunctionComponent<{
    worker: WorkerResource,
    remainingTime: RemainingTime | undefined,
    revalidateRemainingTime: () => Promise<boolean>
}> = props => {
    const [selectedDate, setSelectedDate] = useState<Date>(new Date());
    const {data: tickets, error: ticketsError, revalidate: revalidateCache} = useSWR<TicketResource[]>(
        () => "/workers/" + props.worker.id + "/tickets",
        () => Backend.getTickets(props.worker.id),
        {suspense: true});
    const {data: officeHours, error: officeHoursError} = useSWR<OfficeHoursResource[]>(
        () => "/workers/" + props.worker.id + "/office-hours",
        () => Backend.getOfficeHours(props.worker.id),
        {initialData: []});
    const {t} = useTranslation();

    const createTicket = () => {
        const date = moment(selectedDate).format("YYYY-MM-DD")
        Backend.createTicketForDay(props.worker.id, date)
            .finally(() => {
                revalidateCache();
                props.revalidateRemainingTime();
            });
    };

    const removeTicket = () => {
        const date = moment(selectedDate).format("YYYY-MM-DD")
        Backend.removeTicketFromDay(props.worker.id, date)
            .finally(() => {
                revalidateCache();
                props.revalidateRemainingTime();
            });
    }

    let items: JSX.Element | null = null;
    if (selectedDate < yesterday(new Date())) {
        const officeHoursToday = officeHours?.filter(officeHour => moment(moment(officeHour.arrive).format("YYYY-MM-DD"))
            .isSame(moment(selectedDate)))
        const hasOfficeHours = (officeHoursToday?.length ?? 0) > 0;

        items = (
            <div>
                <h1 className={workerStyle.BigText}>
                    {t("worker.officeHours")}
                </h1>
                <div>
                    {
                        hasOfficeHours ? (
                            officeHoursToday?.map((officeHour, i) => {
                                return (
                                    <p className={workerStyle.SmallText} id={"offtime:" + i}>
                                        {moment(officeHour.arrive).format("HH:MM:SS")}
                                        ->
                                        {moment(officeHour.leave).format("HH:MM:SS")}
                                    </p>
                                )
                            })
                        ) : (
                            <p className={workerStyle.SmallText}>
                                {t("worker.noOfficeHours")}
                            </p>
                        )
                    }
                </div>
            </div>
        )
    } else {
        const ticketsToday = tickets?.filter(ticket => moment(ticket.targetDay)
            .isSame(moment(moment(selectedDate).format("YYYY-MM-DD"))));
        const hasTicket = (ticketsToday?.length ?? 0) > 0;

        items = (
            <div>
                <h1 className={workerStyle.BigText}>
                    {t("worker.tickets")}:
                </h1>
                <div>
                    {hasTicket ? (
                            <>
                                <h3 className={workerStyle.SmallText}>
                                    {t("worker.yesTickets")}
                                </h3>
                                <div className={workerStyle.ButtonWrapper}>
                                    <Online>
                                        <button className={workerStyle.DeleteButton} onClick={removeTicket}>
                                            {t("worker.cancel")}
                                        </button>
                                    </Online>
                                </div>
                            </>
                        )
                        : (
                            <>
                                <h3 className={workerStyle.SmallText}>
                                    {t("worker.noTickets")}
                                </h3>
                                <div className={workerStyle.ButtonWrapper}>
                                    <Online>
                                        <button className={workerStyle.CreateButton} onClick={createTicket}>
                                            {t("worker.create")}
                                        </button>
                                    </Online>
                                </div>
                            </>
                        )}
                </div>
            </div>
        )
        if (moment(moment(new Date()).format("YYYY-MM-DD")).isSame(moment(moment(selectedDate).format("YYYY-MM-DD")))
            && props.remainingTime?.status === "InOffice") {
            items = null;
        }
    }

    return (
        <div>
            <div style={{margin: "0 auto", width: 350, overflow: "auto"}}>
                <Calendar onChange={(date) => {
                    if (!Array.isArray(date))
                        setSelectedDate(date);
                }}
                          selectRange={false}
                          minDate={firstDayInPreviousMonth(new Date())}
                          maxDate={firstDayInTwoMonths(new Date())}/>
            </div>
            {items}
        </div>
    )
};

export default WorkerArea;