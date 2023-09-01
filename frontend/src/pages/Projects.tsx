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
import { ProjectList } from "../components/ProjectList";
import { useContext, useState } from "react";
import { ProjectsTableView } from "../components/ProjectsTableView";
import ProjectContext from "../context/ProjectContext";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { useNavigate } from "react-router-dom";
import * as Paths from "../Paths";
import { StaticDatePicker } from "@mui/x-date-pickers";
import dayjs from "dayjs";

export const Projects = () => {
    const { selectedProject, projects } = useContext(ProjectContext);
    const [tableview, setTableView] = useState(false);
    const handleTableSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
        setTableView(e.target.checked);
    };
    const navigate = useNavigate();
    const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);
    const handleClose = () => {
        setAnchorEl(null);
    };
    const [selectedDate, setSelectedDate] = useState<any>();
    const open = Boolean(anchorEl);
    const id = open ? "simple-popover" : undefined;
    return (
        <Stack
            direction="row"
            justifyContent="space-between"
        >
            <Box  overflow="auto" p={0.3}>
                <Search
                    label="Zoeken..."
                    searchList={projects.map((p) => p.project)}
                />
                <ProjectList
                    projectList={
                        selectedProject
                            ? [selectedProject]
                            : projects.map((p) => p.project)
                    }
                />
            </Box>
            <Stack direction="column">
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
                        <StaticDatePicker
                            defaultValue={dayjs("2022-04-17")}
                            onChange={(newValue) => setSelectedDate(newValue)}
                        />
                    </Popover>
                </Stack>
                <Stack direction="row" justifyContent="flex-end" alignItems="center" border="solid 1px #ddd" p={0.5}>
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
                    <AddCircleIcon
                        color="info"
                        sx={{ fontSize: "45px", cursor: "pointer" }}
                        onClick={() => navigate(Paths.projectAdd.path)}
                    />
                </Stack>
                {!tableview && <Map style={{ width: "100%" }} />}
                {tableview && <ProjectsTableView />}
            </Stack>
        </Stack>
    );
};
