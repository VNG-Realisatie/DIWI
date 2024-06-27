export const calculateTotalYear = (sDate: string | undefined, eDate: string | undefined) => {
    const endDate = new Date(eDate ?? "");
    const startDate = new Date(sDate ?? "");

    const differenceInMilliseconds = endDate.getTime() - startDate.getTime();

    const differenceInYears = differenceInMilliseconds / (1000 * 60 * 60 * 24 * 365);
    return differenceInYears.toFixed(0);
};
