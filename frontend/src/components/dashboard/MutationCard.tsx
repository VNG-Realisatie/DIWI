import { Grid } from "@mui/material";
import { t } from "i18next";
import { WizardCard } from "../project-wizard/WizardCard";
import { LabelComponent } from "../project/LabelComponent";
import { CellContainer } from "../project/project-with-house-block/CellContainer";
type Props = {
    demolitionAmount: number;
    constructionAmount: number;
};

export const MutationCard = ({ demolitionAmount, constructionAmount }: Props) => {
    const readOnly = true;
    return (
        <Grid item xs={12} m={{ xs: 0, md: 1 }}>
            <WizardCard>
                <Grid container spacing={2} alignItems="stretch">
                    {/* Demolition */}
                    <Grid item xs={12} md={4}>
                        <LabelComponent
                            required={false}
                            readOnly={readOnly}
                            text={t("createProject.houseBlocksForm.demolition")}
                            tooltipInfoText={t("tooltipInfo.sloop.title")}
                        />
                        <CellContainer>
                            <LabelComponent required={false} readOnly={readOnly} text={demolitionAmount.toString()} />
                        </CellContainer>
                    </Grid>
                    {/* Construction */}
                    <Grid item xs={12} md={4}>
                        <LabelComponent
                            required={false}
                            readOnly={readOnly}
                            text={t("createProject.houseBlocksForm.grossPlanCapacity")}
                            tooltipInfoText={t("tooltipInfo.brutoPlancapaciteit.title")}
                        />
                        <CellContainer>
                            <LabelComponent required={false} readOnly={readOnly} text={constructionAmount.toString()} />
                        </CellContainer>
                    </Grid>
                    {/* Total */}
                    <Grid item xs={12} md={4}>
                        <LabelComponent
                            required={false}
                            readOnly={readOnly}
                            text={t("createProject.houseBlocksForm.netPlanCapacity")}
                            tooltipInfoText={t("tooltipInfo.nettoPlancapaciteit.title")}
                        />
                        <CellContainer>
                            <LabelComponent required={false} readOnly={readOnly} text={(constructionAmount - demolitionAmount).toString()} />
                        </CellContainer>
                    </Grid>
                </Grid>
            </WizardCard>
        </Grid>
    );
};
