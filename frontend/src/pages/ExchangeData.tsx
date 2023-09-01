import { Button, Stack, Typography } from "@mui/material";
import { DataCardItem } from "../components/DataCardItem";
import  excelIcon  from "../assets/excel.svg";
import xLogoIcon from "../assets/xlogo.svg";
import  roxitIcon from "../assets/roxit.svg";
import  nlMapIcon from "../assets/nl.svg";

import * as Paths from "../Paths";

export const ExchangeData = () => {
  return (
    <Stack border="solid 1px #ddd" py={5} px={8} >
      <Typography fontSize="20px" fontWeight="600">
        Data uitwisselen
      </Typography>
      <Typography fontSize="16px" mt={2}>
        Importeren:
      </Typography>
      <Stack mt={1} direction="row" alignItems="flex-start" justifyContent="flex-start" flexWrap="wrap">
        <DataCardItem text="Excel" link={Paths.importExcel.path}  isImport >
            <img src={excelIcon} alt='excel'/>
        </DataCardItem>
        <DataCardItem text="Squit" link="" isImport >
            <img src={roxitIcon} alt='squit'/>
        </DataCardItem>
        <DataCardItem text="Systeem x" link=""  isImport >
            <img src={xLogoIcon} alt='system'/>
        </DataCardItem>
      </Stack>
      <Typography fontSize="16px" mt={2}>
        Exporteren:
      </Typography>
      <Stack mt={1} direction="row" alignItems="flex-start" justifyContent="flex-start" flexWrap="wrap">
        <DataCardItem text="Excel" link={Paths.exportExcel.path}  >
            <img src={excelIcon} alt='excel'/>
        </DataCardItem>
        <DataCardItem text="Provincie" link="">
            <img src={nlMapIcon} alt='Provincie'/>
        </DataCardItem>
      </Stack>
      <Button sx={{width:"40%",mt:2}}  variant="outlined">Nieuwe data koppeling instellen</Button>
    </Stack>
  );
};
