import { useParams } from "react-router-dom";

export const ProjectDetail=()=>{
    let { id } = useParams();
    return <>Detail Page -{id}</>
}