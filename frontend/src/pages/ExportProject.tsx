import { Stack, Typography } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import { ProjectsTableView } from "../components/project/ProjectsTableView";
type Props = {
    excelExport: boolean;
};
export const ExportProject = ({ excelExport }: Props) => {
    return (
        <Stack pb={10}>
            <Typography fontSize="20px" fontWeight="600">
                {excelExport ? "Exporteer naar excel" : "Exporteer naar provincie"}
            </Typography>
            <Typography fontSize="16px" mt={2}>
                Kies peildatum:
            </Typography>
            <DatePicker />
            <Typography fontSize="16px" mt={2}>
                Project overzicht:
            </Typography>
            {/* ToDo handle selected row later */}
            <ProjectsTableView showCheckBox />
        </Stack>
    );
};
