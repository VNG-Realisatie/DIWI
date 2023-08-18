import * as React from "react";
import TextField from "@mui/material/TextField";
import Stack from "@mui/material/Stack";
import Autocomplete from "@mui/material/Autocomplete";
import { SearchItem } from "../api/dummyData";

type SearchProps = {
  searchList: Array<SearchItem>;
  label: string;
  setSearchParam: (sparam: SearchItem) => void;
  searchParam: SearchItem|null;
};
export default function Search({ searchList, label,setSearchParam,searchParam }: SearchProps) {
  return (
    <Stack spacing={2}>
      <Autocomplete
        options={searchList}
        getOptionLabel={(option: SearchItem) => option?option.name:""}
        value={searchParam}
        onChange={(event: any, newValue: SearchItem ) => {
          setSearchParam(newValue);
      }}
        renderInput={(params) => (
          <TextField
            {...params}
            label={label}
          />
        )}
      />
    </Stack>
  );
}
