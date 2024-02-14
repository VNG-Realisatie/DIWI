export const filterDataWithSelectedColumns = (projects: any[], filterColumns: string[]): Partial<any>[] => {
    return projects.map((project) => {
        const filteredProject: Partial<any> = {};
        filterColumns.forEach((column) => {
            if (project[column]) {
                filteredProject[column] = project[column];
            }
        });
        return filteredProject;
    });
};
