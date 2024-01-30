import { Button, Stack, Typography } from "@mui/material";
import { DataCardItem } from "../components/DataCardItem";
import  excelIcon  from "../assets/excel.svg";
import xLogoIcon from "../assets/xlogo.svg";
import  roxitIcon from "../assets/roxit.svg";
import  nlMapIcon from "../assets/nl.svg";

import * as Paths from "../Paths";
import { useTranslation } from "react-i18next";

export const ExchangeData = () => {
    const {t}=useTranslation();
  return (
    <Stack border="solid 1px #ddd" py={5} px={8} mb={10}>
      <Typography fontSize="20px" fontWeight="600">
        {t("exchangeData.title")}
      </Typography>
      <Typography fontSize="16px" mt={2}>
      {t("exchangeData.import")}
      </Typography>
      <Stack mt={1} direction="row" alignItems="flex-start" justifyContent="flex-start" flexWrap="wrap">
        <DataCardItem text={t("exchangeData.excel")} link={Paths.importExcel.path}  isImport >
            <img src={excelIcon} alt='excel'/>
        </DataCardItem>
        <DataCardItem text={t("exchangeData.squit")} link={Paths.importSquitProjects.path} isImport >
            <img src={roxitIcon} alt='squit'/>
        </DataCardItem>
        <DataCardItem text={t("exchangeData.system")} link=""  isImport >
            <img src={xLogoIcon} alt='system'/>
        </DataCardItem>
      </Stack>
      <Typography fontSize="16px" mt={2}>
      {t("exchangeData.export")}
      </Typography>
      <Stack mt={1} direction="row" alignItems="flex-start" justifyContent="flex-start" flexWrap="wrap">
        <DataCardItem text={t("exchangeData.excel")} link={Paths.exportExcel.path}  >
            <img src={excelIcon} alt='excel'/>
        </DataCardItem>
        <DataCardItem text={t("exchangeData.province")} link={Paths.exportProvince.path}>
            <img src={nlMapIcon} alt='Provincie'/>
        </DataCardItem>
      </Stack>
      <Button sx={{width:"40%",mt:2}}  variant="outlined">{t("exchangeData.newData")}</Button>
    </Stack>
  );
};
