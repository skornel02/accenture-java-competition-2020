import moment, {Moment} from "moment";

/* in memoriam in stack redundantiam */

export function firstDayInPreviousMonth(yourDate: Date) {
    const d = new Date(yourDate);
    d.setDate(1);
    d.setMonth(d.getMonth() - 1);
    return d;
}

export function firstDayInTwoMonths(yourDate: Date) {
    const d = new Date(yourDate);
    d.setDate(1);
    d.setMonth(d.getMonth() + 2);
    return d;
}

export function previousDay(yourDate: Date) {
    const d = new Date(yourDate);
    d.setDate(d.getDate() - 1);
    return d;
}

export function isSameDate(d1: Date, d2: Date): boolean {
    return moment(moment(d1).format("YYYY-MM-DD")).isSame(moment(d2).format("YYYY-MM-DD"))
}

export function isSameDateMoment(d1: Moment, d2: Moment) {
    return moment(d1.format("YYYY-MM-DD")).isSame(d2.format("YYYY-MM-DD"))
}

export function isDateBeforeDate(d1: Moment, d2: Moment) {
    return moment(d1.format("YYYY-MM-DD")).isBefore(d2.format("YYYY-MM-DD"))
}

export function isDateAfterDate(d1: Moment, d2: Moment) {
    return moment(d1.format("YYYY-MM-DD")).isAfter(d2.format("YYYY-MM-DD"))
}