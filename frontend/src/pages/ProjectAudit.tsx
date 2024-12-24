import { useState, useEffect } from "react";
import { Box, Button, Typography } from "@mui/material";
import { getProjectAudit, ProjectAudit } from "../api/projectsServices";
import TableComponent from "../components/project/TableComponent";
import DateInput from "../components/project/inputs/DateInput";
import dayjs, { Dayjs } from "dayjs";
import { useParams } from "react-router-dom";

const ProjectAuditTable = () => {
    const [auditLogs, setAuditLogs] = useState<ProjectAudit[]>([]);
    const [startDate, setStartDate] = useState<Dayjs | null>(null);
    const [endDate, setEndDate] = useState<Dayjs | null>(null);

    const { projectId } = useParams<{ projectId: string }>();

    const fetchAuditLogs = async () => {
        const formattedStartDate = startDate ? encodeURIComponent(startDate.format("YYYY-MM-DDTHH:mm:ss")) : "";
        const formattedEndDate = endDate ? encodeURIComponent(endDate.format("YYYY-MM-DDTHH:mm:ss")) : "";
        const query = `projectId=${projectId}&startTime=${formattedStartDate}&endTime=${formattedEndDate}`;
        const logs = await getProjectAudit(query);
        setAuditLogs(logs);
    };

    useEffect(() => {
        if (!startDate || !endDate) return;
        fetchAuditLogs();
    }, [startDate, endDate]);

    const columns = [
        { field: "action", headerName: "Action", flex: 1 },
        { field: "property", headerName: "Property", flex: 1 },
        {
            field: "oldValues",
            headerName: "Old Values",
            flex: 2,
            renderCell: (params: { row: { oldValues?: string[] } }) => {
                return <Typography>{params.row?.oldValues?.join(", ") || ""}</Typography>;
            },
        },
        {
            field: "newValues",
            headerName: "New Values",
            flex: 2,
            renderCell: (params: { row: { newValues?: string[] } }) => {
                return <Typography>{params.row?.newValues?.join(", ") || ""}</Typography>;
            },
        },
        { field: "changeUser", headerName: "Change User", flex: 1 },
        {
            field: "changeDate",
            headerName: "Change Date",
            flex: 1,
            renderCell: (params: { row: { changeDate: string } }) => {
                return <Typography>{dayjs(params.row.changeDate).format("YYYY-MM-DD HH:mm:ss")}</Typography>;
            },
        },
    ];

    return (
        <Box style={{ marginBottom: "40px" }}>
            <Box style={{ display: "flex", justifyContent: "space-between", marginBottom: "20px", gap: "40px" }}>
                <DateInput
                    value={startDate ? startDate.format("YYYY-MM-DD") : null}
                    setValue={setStartDate}
                    readOnly={false}
                    mandatory={true}
                    errorText="Start date is required"
                    title="Start Date"
                />
                <DateInput
                    value={endDate ? endDate.format("YYYY-MM-DD") : null}
                    setValue={setEndDate}
                    readOnly={false}
                    mandatory={true}
                    errorText="End date is required"
                    title="End Date"
                />
            </Box>

            <TableComponent
                showCheckBox={false}
                rows={auditLogs.map((log, index) => ({ ...log, id: log.changeDate ? log.changeDate + index : index }))}
                columns={columns}
                rowCount={auditLogs.length}
            />
        </Box>
    );
};

export default ProjectAuditTable;
