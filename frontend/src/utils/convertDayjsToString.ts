import { Dayjs } from "dayjs";
import { dateFormats } from "../localization";

export const convertDayjsToString = (date: Dayjs) => {
    date.format(dateFormats.keyboardDate);
    return `${date.date() < 10 ? 0 : ""}${date.date()}-${date.month() + 1 < 10 ? 0 : ""}${date.month() + 1}-${date.year()}`;
};
