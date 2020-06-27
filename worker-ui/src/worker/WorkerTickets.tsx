import React from "react";
import workerStyle from "../resource/style/worker.module.css";
import {useAuthInformation} from "../authentication/SecuredArea";
import {useSelectedDate} from "./WorkerDateSelector";
import {useTranslation} from "react-i18next";
import useSWR from "swr";
import {TicketResource} from "../resource/Resources";
import Backend from "../resource/Backend";
import {isSameDateMoment} from "../utility/DateUtils";
import moment from "moment";
// @ts-ignore
import {Online} from 'react-detect-offline'
import {confirmAlert} from "react-confirm-alert";
import 'react-confirm-alert/src/react-confirm-alert.css'; // Import cs

const WorkerTickets: React.FunctionComponent = () => {
    const info = useAuthInformation();
    const worker = info.worker!;
    const selectedDate = useSelectedDate();
    const {t} = useTranslation();
    const {data: tickets, revalidate: reValidateCache} = useSWR<TicketResource[]>(
        () => "/workers/" + worker.id + "/tickets",
        () => Backend.getTickets(worker.id),
        {suspense: true});
    const ticketsToday = tickets?.filter(ticket => isSameDateMoment(moment(ticket.targetDay), selectedDate)) ?? [];

    const createTicket = () => {
        confirmAlert({
            title: t("worker.areYouSure"),
            message: t("worker.areYouSureCreate"),
            buttons: [
                {label: t("worker.areYouSureCancel"), onClick: () => {} },
                {
                    label: t("worker.areYouSureYes"),
                    onClick: () => {
                        const date = moment(selectedDate).format("YYYY-MM-DD")
                        Backend.createTicketForDay(worker.id, date)
                            .finally(() => {
                                reValidateCache().then(r => {});
                            });
                    }
                }
            ]
        })
    };

    const removeTicket = () => {
        confirmAlert({
            title: t("worker.areYouSure"),
            message: t("worker.areYouSureRemove"),
            buttons: [
                {label: t("worker.areYouSureCancel"), onClick: () => {} },
                {
                    label: t("worker.areYouSureYes"),
                    onClick: () => {
                        const date = moment(selectedDate).format("YYYY-MM-DD")
                        Backend.removeTicketFromDay(worker.id, date)
                            .finally(() => {
                                reValidateCache().then(r => {});
                            });
                    }
                }
            ]
        })
    }

    return (
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
    );
};

export default WorkerTickets;