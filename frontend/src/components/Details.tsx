import { List, ListItem, ListItemText } from "@mui/material";
//DoTo this type will be updated after real data
export type Project={ id: number; name: string,color:string,organization:string,geo:string };
type Props={
    project:Project
}
export const Details=({project}:Props)=>{
    return (
        <List
        sx={{
          bgcolor: "background.paper",
          width: "100%",
        }}
      >
         
         {project&&Object.entries(project).map(([property, value]) => (
       <>
        <ListItem
          key={property}
          sx={{
            backgroundColor:"#738092",
            color:"#FFFFFF",
            border: "solid 1px #ddd",
          }}
        >
          <ListItemText primary={property} />
        </ListItem>
        <ListItem
        key={value}
        sx={{
          border: "solid 1px #ddd",
        }}
      >
        <ListItemText primary={value} />
      </ListItem>
       </>
        
      ))}
      </List>
    );
}