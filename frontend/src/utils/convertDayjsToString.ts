import { Dayjs } from "dayjs";

export const convertDayjsToString = (date: Dayjs) => {
    date.format("DD-MM-YYYY");
    return `${date.date() < 10 ? 0 : ""}${date.date()}-${date.month() + 1 < 10 ? 0 : ""}${date.month() + 1}-${date.year()}`;
};
