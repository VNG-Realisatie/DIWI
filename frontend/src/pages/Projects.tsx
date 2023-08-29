import {
    Box,
    FormControl,
    FormControlLabel,
    Popover,
    Stack,
    Switch,
    Typography,
} from "@mui/material";
import { ReactComponent as Map } from "../assets/temp/map.svg";
import Search from "../components/Search";
import { projects } from "../api/dummyData";
import { ProjectList } from "../components/ProjectList";
import { useContext, useState } from "react";
import { ProjectsTableView } from "../components/ProjectsTableView";
import ProjectContext from "../context/ProjectContext";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { useNavigate } from "react-router-dom";
import * as Paths from "../Paths";
import { LocalizationProvider, StaticDatePicker } from "@mui/x-date-pickers";
import dayjs from "dayjs";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

export const Projects = () => {
    const { selectedProject } = useContext(ProjectContext);
    const [tableview, setTableView] = useState(false);
    const handleTableSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
        setTableView(e.target.checked);
    };
    const navigate = useNavigate();
    const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);
    const handleClose = () => {
        setAnchorEl(null);
    };
    const [selectedDate,setSelectedDate]=useState<any>()
    const open = Boolean(anchorEl);
    const id = open ? "simple-popover" : undefined;
    return (
        <Stack
            direction="row"
            justifyContent="space-between"
            maxHeight="81vh"
            position="relative"
        >
            <Box width="20%" overflow="auto" p={0.3}>
                <Search label="Zoeken..." searchList={projects} />
                <ProjectList
                    projectList={selectedProject ? [selectedProject] : projects}
                />
            </Box>
            <Box>
                <Stack
                    direction="row"
                    alignItems="center"
                    justifyContent="space-between"
                    sx={{ backgroundColor: "#002C64", color: "#FFFFFF" }}
                    p={1}
                >
                    <Typography> Projecten overzicht: </Typography>
                    <Typography
                        onClick={(
                            event: React.MouseEvent<HTMLButtonElement>
                        ) => {
                            setAnchorEl(event.currentTarget);
                        }}
                    >
                        Pijldatum: {"2022-04-17"}
                    </Typography>
                    <Popover
                        id={id}
                        open={open}
                        anchorEl={anchorEl}
                        onClose={handleClose}
                        anchorOrigin={{
                            vertical: "bottom",
                            horizontal: "left",
                        }}
                    >
                        <LocalizationProvider dateAdapter={AdapterDayjs}>
                            <StaticDatePicker
                                defaultValue={dayjs("2022-04-17")}
                                onChange={(newValue)=>setSelectedDate(newValue)}
                            />
                        </LocalizationProvider>
                    </Popover>
                </Stack>
                <Stack direction="row" justifyContent="flex-end">
                    <FormControl component="fieldset" variant="standard">
                        <FormControlLabel
                            control={
                                <Switch
                                    checked={tableview}
                                    onChange={handleTableSelect}
                                    name="table"
                                />
                            }
                            label="Tabel weergave "
                        />
                    </FormControl>
                </Stack>
                {!tableview && <Map />}
                {tableview && <ProjectsTableView />}
            </Box>
            <Box
                position="absolute"
                right="30px"
                bottom="80px"
                sx={{ cursor: "pointer" }}
                onClick={() => navigate(Paths.projectAdd.path)}
            >
                <AddCircleIcon color="info" sx={{ fontSize: "58px" }} />
            </Box>
        </Stack>
    );
};
