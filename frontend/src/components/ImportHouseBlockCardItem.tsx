import { Grid, Typography } from "@mui/material"
import { columnTitleStyle } from "./ImportProjectCardItem";

export const ImportHouseBlockCardItem=(props:any)=>{
    const {hb,isImportCard}=props;
   return <>
                                <Grid
                                    item
                                    sm={12}
                                    sx={{
                                        backgroundColor: "#00A9F3",
                                        color: "#FFFFFF",
                                        p: 1,
                                    }}
                                    display="flex"
                                    justifyContent="space-between"
                                >
                                    {isImportCard?hb.name:hb.naam?hb.naam:"Geen Naam"}
                                </Grid>
                                <Grid item sm={2}>
                                    <Typography
                                        sx={columnTitleStyle}
                                    >
                                        StartDatum
                                    </Typography>

                                    <Typography
                                        sx={{
                                            border: "solid 1px #ddd",
                                            p: 0.5,
                                        }}
                                    >
                                        {isImportCard?hb.startdate:hb["start datum"]}
                                    </Typography>
                                </Grid>
                                <Grid item sm={2}>
                                    <Typography
                                        sx={columnTitleStyle}
                                    >
                                        EindDatum
                                    </Typography>

                                    <Typography
                                        sx={{
                                            border: "solid 1px #ddd",
                                            p: 0.5,
                                        }}
                                    >
                                        {isImportCard?hb.finishDate:hb["eind datum"]}
                                    </Typography>
                                </Grid>
                                <Grid item sm={2}>
                                    <Typography
                                        sx={columnTitleStyle}
                                    >
                                        Mutatiesoort
                                    </Typography>

                                    <Typography
                                        sx={{
                                            border: "solid 1px #ddd",
                                            p: 0.5,
                                        }}
                                    >
                                        {isImportCard?hb.mutationType:hb["mutatie_soort"]}
                                    </Typography>
                                </Grid>
                                <Grid item sm={2}>
                                    <Typography
                                        sx={columnTitleStyle}
                                    >
                                        Bruto plancapaciteit
                                    </Typography>

                                    <Typography
                                        sx={{
                                            border: "solid 1px #ddd",
                                            p: 0.5,
                                        }}
                                    >
                                        {isImportCard?hb.grossPlanCapacity:hb["bruto_plancapaciteit"]}
                                    </Typography>
                                </Grid>
                                <Grid item sm={2}>
                                    <Typography
                                        sx={columnTitleStyle}
                                    >
                                        Sloop
                                    </Typography>

                                    <Typography
                                        sx={{
                                            border: "solid 1px #ddd",
                                            p: 0.5,
                                        }}
                                    >
                                        {isImportCard?hb.demolition:hb.sloop}
                                    </Typography>
                                </Grid>
                                <Grid item sm={2}>
                                    <Typography
                                        sx={columnTitleStyle}
                                    >
                                        Netto Plancapaciteit
                                    </Typography>

                                    <Typography
                                        sx={{
                                            border: "solid 1px #ddd",
                                            p: 0.5,
                                        }}
                                    >
                                        {isImportCard?hb.netPlanCapacity:hb["netto_plancapaciteit"]}
                                    </Typography>
                                </Grid>
                                <Grid item sm={2}>
                                    <Typography
                                        sx={columnTitleStyle}
                                    >
                                        Grootte
                                    </Typography>

                                    <Typography
                                        sx={{
                                            border: "solid 1px #ddd",
                                            p: 0.5,
                                        }}
                                    >
                                        {isImportCard?hb.size:hb.grootte}
                                    </Typography>
                                </Grid>
                                <Grid item sm={2}>
                                    <Typography
                                        sx={columnTitleStyle}
                                    >
                                      {isImportCard?"Koopwoning":"Buurt"}  Koopwoning
                                    </Typography>

                                    <Typography
                                        sx={{
                                            border: "solid 1px #ddd",
                                            p: 0.5,
                                        }}
                                    >
                                        {isImportCard?hb.ownerOccupied:hb.buurt}
                                    </Typography>
                                </Grid>
                                <Grid item sm={3}>
                                    <Typography
                                        sx={columnTitleStyle}
                                    >
                                        {isImportCard?"Huurwoning Particuliere verhuurder":"Wijk"}
                                    </Typography>

                                    <Typography
                                        sx={{
                                            border: "solid 1px #ddd",
                                            p: 0.5,
                                        }}
                                    >
                                        {isImportCard?hb.rentalPrivateHouse:hb.wijk}
                                    </Typography>
                                </Grid>
                                <Grid item sm={3}>
                                    <Typography
                                        sx={columnTitleStyle}
                                    >
                                       {isImportCard?"Huurwoning Woningcorporatie":"Fysiek Voorkomen"}
                                    </Typography>

                                    <Typography
                                        sx={{
                                            border: "solid 1px #ddd",
                                            p: 0.5,
                                        }}
                                    >
                                        {isImportCard?hb.rentalHousingCompany:hb.fysiek_voorkomen}
                                    </Typography>
                                </Grid>
                                <Grid item sm={2}>
                                    <Typography
                                        sx={columnTitleStyle}
                                    >
                                        Waarde
                                    </Typography>

                                    <Typography
                                        sx={{
                                            border: "solid 1px #ddd",
                                            p: 0.5,
                                        }}
                                    >
                                        {isImportCard?hb.price:hb.waarde}
                                    </Typography>
                                </Grid>
                                <Grid item sm={2}>
                                    <Typography
                                        sx={columnTitleStyle}
                                    >
                                        Huurbedrag
                                    </Typography>

                                    <Typography
                                        sx={{
                                            border: "solid 1px #ddd",
                                            p: 0.5,
                                        }}
                                    >
                                        {isImportCard?hb.rentingPrice:hb.huurbedrag}
                                    </Typography>
                                </Grid>
                                <Grid item sm={2} mb={6}>
                                    <Typography
                                        sx={columnTitleStyle}
                                    >
                                        Woningtype:
                                    </Typography>

                                    <Typography
                                        sx={{
                                            border: "solid 1px #ddd",
                                            p: 0.5,
                                        }}
                                    >
                                        {isImportCard?hb.houseType:hb.grondpositie}
                                    </Typography>
                                </Grid>
                            </>
}
