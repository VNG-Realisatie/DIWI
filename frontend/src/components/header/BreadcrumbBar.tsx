import React, { useContext } from 'react';
import { Stack, Typography, Popover, Box } from '@mui/material';
import { DateCalendar } from '@mui/x-date-pickers';
import Search from '../Search';
import dayjs from 'dayjs';
import ProjectContext from '../../context/ProjectContext';

interface BreadcrumbBarProps {
  breadcrumb: string[];
}

const BreadcrumbBar: React.FC<BreadcrumbBarProps> = ({ breadcrumb }) => {
    const { projects } = useContext(ProjectContext);
  const [anchorEl, setAnchorEl] = React.useState<HTMLButtonElement | null>(null);
  const [selectedDate, setSelectedDate] = React.useState<any>();

  const handleDateClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const open = Boolean(anchorEl);
    const openid = open ? "simple-popover" : undefined;

  const convertedDate = selectedDate?.toISOString().split('T')[0] ?? dayjs().toISOString().split('T')[0];

  return (

            <Stack
                direction="row"
                justifyContent="flex-start"
                alignItems="flex-start"
            >
                <Box width="25%">
                    <Search
                        label="Zoeken..."
                        searchList={projects.map((p) => p.project)}
                        isDetailSearch={true}
                    />
                </Box>
                <Stack
                    width="75%"
                    direction="row"
                    alignItems="center"
                    justifyContent="space-between"
                    sx={{ backgroundColor: "#002C64", color: "#FFFFFF" }}
                    p={1}
                >

        <Typography>{breadcrumb.join(' / ')}</Typography>
        <Typography onClick={handleDateClick}>Peildatum: {convertedDate}</Typography>
        <Popover
          id={openid}
          open={open}
          anchorEl={anchorEl}
          onClose={handleClose}
          anchorOrigin={{
            vertical: 'bottom',
            horizontal: 'left',
          }}
        >
            <DateCalendar defaultValue={selectedDate ? selectedDate : dayjs()}
                onChange={(newValue) => {
                setSelectedDate(newValue);
                handleClose();
    }} />
        </Popover>

        </Stack>
    </Stack>
  );
};

export default BreadcrumbBar;
