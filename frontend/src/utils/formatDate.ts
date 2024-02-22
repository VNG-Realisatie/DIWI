export const formatDate = (date: string | null | undefined) => {
    if (date !== null && date !== undefined) {
        const dateArray = date.split("-");
        const formattedDate = `${dateArray[2]}-${dateArray[1]}-${dateArray[0]}`;
        return formattedDate;
    } else if (date === null || date === undefined) {
        return "";
    }
};
