import { Button, Stack, Typography } from "@mui/material";
import { DataCardItem } from "../components/DataCardItem";
import { ReactComponent as Excel } from "../assets/excel.svg";
import { ReactComponent as xlogo } from "../assets/xlogo.svg";
import { ReactComponent as Roxit } from "../assets/roxit.svg";
import { ReactComponent as NlMap } from "../assets/nl.svg";
import * as Paths from "../Paths";


export const ExchangeData = () => {
  return (
    <Stack border="solid 1px #ddd" py={5} px={8} >
      <Typography fontSize="20px" fontWeight="600">
        Data iutwisselen
      </Typography>
      <Typography fontSize="16px" mt={2}>
        Importen:
      </Typography>
      <Stack mt={1} direction="row" alignItems="center" justifyContent="space-between" width="60%">
        <DataCardItem text="Excel" link="" icon={Excel} isImport />
        <DataCardItem text="Squit" link="" icon={Roxit} isImport />
        <DataCardItem text="Systeem x" link="" icon={xlogo} isImport />
      </Stack>
      <Typography fontSize="16px" mt={2}>
        Exporteren:
      </Typography>
      <Stack mt={1} direction="row" alignItems="center" justifyContent="space-between" width="39%">
        <DataCardItem text="Excel" link={Paths.exportExcel.path} icon={Excel}  />
        <DataCardItem text="Squit" link="" icon={NlMap}  />
      </Stack>
      <Button sx={{width:"40%",mt:2}}  variant="outlined">Nieuwe data koppeling instellen</Button>
    </Stack>
  );
};
