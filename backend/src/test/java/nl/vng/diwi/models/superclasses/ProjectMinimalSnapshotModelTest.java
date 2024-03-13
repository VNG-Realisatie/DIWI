package nl.vng.diwi.models.superclasses;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;

class ProjectMinimalSnapshotModelTest {

    private static Stream<Arguments> validation () {
        return Stream.of(
             Arguments.of("projectId", null),
             Arguments.of("projectStateId", null),
             Arguments.of("projectName", null),
             Arguments.of("projectColor", null),
             Arguments.of("projectColor", "not a color"),
             Arguments.of("confidentialityLevel", null),
             Arguments.of("projectPhase", null)
        );
    }

    @Test
    void validateOk() {
        var model = createModel();

        var result = model.validate();

        assertThat(result).isNull();
    }

    @ParameterizedTest
    @MethodSource("validation")
    void validateError(String expected, Object argument) throws Exception {
        var model = createModel();

        Stream.of(model.getClass().getMethods())
            .filter(m -> m.getName().equals( "set" + StringUtils.capitalize(expected)))
            .findFirst()
            .get()
            .invoke(model, argument);

        var result = model.validate();
        assertThat(result).contains(expected);
    }

    private ProjectMinimalSnapshotModel createModel() {
        var model = new ProjectMinimalSnapshotModel();

        UUID uuid = UUID.randomUUID();
        model.setProjectId(uuid);
        model.setProjectStateId(uuid);
        model.setProjectName("name");
        model.setProjectColor("#abcdef");
        model.setConfidentialityLevel(Confidentiality.OPENBAAR);
        model.setProjectPhase(ProjectPhase._1_INITIATIEFFASE);
        return model;
    }

}
