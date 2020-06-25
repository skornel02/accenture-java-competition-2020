import React, {useState} from "react";
import {OfficeHoursResource, RemainingTime, TicketResource, WorkerResource} from "../resource/Resources";
import useSWR from "swr";
import Backend from "../resource/Backend";
import {useTranslation} from "react-i18next";
import moment from "moment";
import {firstDayInPreviousMonth, firstDayInTwoMonths, isSameDate, previousDay} from "../utility/DateUtils";
import workerStyle from "../resource/style/worker.module.css";
import Calendar from "react-calendar";
// @ts-ignore
import {Online} from 'react-detect-offline'

const WorkerCalendar: React.FunctionComponent<{
    worker: WorkerResource,
    remainingTime: RemainingTime,
    reValidateRemainingTime: () => Promise<boolean>
}> = props => {
    const [selectedDate, setSelectedDate] = useState<Date>(new Date());
    const {data: tickets, revalidate: reValidateCache} = useSWR<TicketResource[]>(
        () => "/workers/" + props.worker.id + "/tickets",
        () => Backend.getTickets(props.worker.id),
        {suspense: true});
    const {data: officeHours} = useSWR<OfficeHoursResource[]>(
        () => "/workers/" + props.worker.id + "/office-hours",
        () => Backend.getOfficeHours(props.worker.id),
        {initialData: []});
    const {t} = useTranslation();

    const createTicket = () => {
        const date = moment(selectedDate).format("YYYY-MM-DD")
        Backend.createTicketForDay(props.worker.id, date)
            .finally(() => {
                reValidateCache();
                props.reValidateRemainingTime();
            });
    };

    const removeTicket = () => {
        const date = moment(selectedDate).format("YYYY-MM-DD")
        Backend.removeTicketFromDay(props.worker.id, date)
            .finally(() => {
                reValidateCache();
                props.reValidateRemainingTime();
            });
    }

    const officeHourFilter = (officeHour: OfficeHoursResource, targetDate: Date): boolean => {
        return isSameDate(new Date(officeHour.leave), targetDate);
    };

    const ticketFilter = (ticket: TicketResource, targetDate: Date): boolean => {
        return isSameDate(moment(ticket.targetDay).toDate(), targetDate);
    }

    let items: JSX.Element | null = null;
    if (selectedDate < previousDay(new Date())) {
        const officeHoursToday = officeHours?.filter(officeHour => officeHourFilter(officeHour, selectedDate)) ?? []

        items = (
            <div>
                <h1 className={workerStyle.BigText}>
                    {t("worker.officeHours")}
                </h1>
                <div>
                    {
                        officeHoursToday.length !== 0 ? (
                            officeHoursToday?.map((officeHour, i) => {
                                return (
                                    <p className={workerStyle.SmallText} id={"timespan:" + i}>
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
        const ticketsToday = tickets?.filter(ticket => ticketFilter(ticket, selectedDate)) ?? [];
        if (!(isSameDate(new Date(), selectedDate) && props.remainingTime.status === "InOffice")) {
            items = (
                <div>
                    <h1 className={workerStyle.BigText}>
                        {t("worker.tickets")}:
                    </h1>
                    <div>
                        {ticketsToday.length !== 0 ? (
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
export default WorkerCalendar;