import {  Stack, Typography } from "@mui/material";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { ProjectsTableView } from "../components/ProjectsTableView";

export const ExportExcel = () => {
    
  return (
    <Stack pb={10}>
      <Typography fontSize="20px" fontWeight="600">
        Exporteer naar provincie
      </Typography>
      <Typography fontSize="16px" mt={2}>
        Kies peildatum:
      </Typography>
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <DatePicker />
      </LocalizationProvider>
      <Typography fontSize="16px" mt={2}>
        Project overzicht:
      </Typography>
      {/* ToDo handle selected row later */}
      <ProjectsTableView showCheckBox />
   
    </Stack>
  );
};
