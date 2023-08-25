import { Stack, Typography } from "@mui/material";
import { DataCardItem } from "../components/DataCardItem";
import { ReactComponent as Excel } from "../assets/excel.svg";
import { ReactComponent as X } from "../assets/X.svg";
import { ReactComponent as Roxit } from "../assets/roxit.svg";


export const ExchangeData = () => {
  return (
    <Stack border="solid 1px #ddd" py={5} px={8}>
      <Typography fontSize="20px" fontWeight="600">
        Data iutwisselen
      </Typography>
      <Typography fontSize="16px" mt={2}>
        Importen:
      </Typography>
      <Stack direction="row" alignItems="center" justifyContent="space-between" width="60%">
        <DataCardItem text="Excel" link="" icon={Excel} isImport />
        <DataCardItem text="Squit" link="" icon={Roxit} isImport />
        <DataCardItem text="Systeem x" link="" icon={X} isImport />

      </Stack>
    </Stack>
  );
};
