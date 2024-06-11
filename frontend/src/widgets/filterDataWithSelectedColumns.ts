// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const filterDataWithSelectedColumns = (projects: any[], filterColumns: string[]): Partial<any>[] => {
    return projects.map((project) => {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        const filteredProject: Partial<any> = {};
        filterColumns.forEach((column) => {
            if (project[column]) {
                filteredProject[column] = project[column];
            }
        });
        return filteredProject;
    });
};
