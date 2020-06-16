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

export function yesterday(yourDate: Date) {
    const d = new Date(yourDate);
    d.setDate(d.getDate() - 1);
    return d;
}