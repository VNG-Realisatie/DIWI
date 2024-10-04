package nl.vng.diwi.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.Dal;
import nl.vng.diwi.dal.DalFactory;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.models.RangeSelectDisabledModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.testutil.TestDb;

public class PropertiesServiceTest {
    private static DalFactory dalFactory;
    private static TestDb testDb;

    @BeforeAll
    static void beforeAll() throws Exception {
        testDb = new TestDb();
        testDb.reset();
    }

    @AfterAll
    static void afterAll() {
        if (testDb != null) {
            testDb.close();
        }
    }

    private ZonedDateTime now;
    private Dal dal;
    private VngRepository repo;
    private User user;
    private UUID userUuid;
    private PropertiesService service;

    @BeforeEach
    void beforeEach() {
        now = ZonedDateTime.now();

        dalFactory = testDb.getDalFactory();
        dal = dalFactory.constructDal();
        repo = new VngRepository(dal.getSession());
        service = new PropertiesService();

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            user = repo.persist(new User());
            userUuid = user.getId();

            transaction.commit();
            repo.getSession().clear();
        }
    }

    @AfterEach
    void afterEach() throws Exception {
        dal.close();
        testDb.reset();
    }

    @ParameterizedTest
    @CsvSource({
            "name,1,,false",
            "name,1,2,false",
            "name,1,1,false",
            "name,1,,true",
            "name,1,2,true",
            "name,1,1,true"
    })
    public void priceCategory(String name, BigDecimal min, BigDecimal max, Boolean disabled) throws Exception {

        String propertyName = "priceRangeBuy";
        UUID propertyUuid = service.getPropertyUuid(repo, propertyName);

        createAndPersistRange(null, name, min, max, disabled, propertyName, propertyUuid);

        var actual = service.getProperty(repo, propertyUuid);

        assertThat(actual.getRanges())
                .extracting("name", "min", "max", "disabled")
                .containsExactly(Tuple.tuple(name, min, max, disabled));
    }

    @ParameterizedTest
    @CsvSource({
            "name,1,,false",
            "name,1,2,false",
            "name,1,1,false",
            "name,1,,true",
            "name,1,2,true",
            "name,1,1,true"
    })
    public void priceCategoryUpdate(String name, BigDecimal min, BigDecimal max, Boolean disabled) throws Exception {

        String propertyName = "priceRangeBuy";
        UUID propertyUuid = service.getPropertyUuid(repo, propertyName);

        var rangeId = createAndPersistRange(null, "a", BigDecimal.ONE, BigDecimal.TEN, false, propertyName, propertyUuid);

        createAndPersistRange(rangeId, name, min, max, disabled, propertyName, propertyUuid);

        var actual = service.getProperty(repo, propertyUuid);

        assertThat(actual.getRanges())
                .extracting("name", "min", "max", "disabled")
                .containsExactly(Tuple.tuple(name, min, max, disabled));
    }

    private UUID createAndPersistRange(
            UUID rangeId,
            String name,
            BigDecimal min,
            BigDecimal max,
            Boolean disabled,
            String propertyName,
            UUID propertyUuid) throws VngNotFoundException, VngBadRequestException {
        var range = new RangeSelectDisabledModel();
        range.setId(rangeId);
        range.setName(name);
        range.setMin(min);
        range.setMax(max);
        range.setDisabled(disabled);

        var model = new PropertyModel();
        model.setId(propertyUuid);
        model.setName(propertyName);
        model.setRanges(List.of(range));

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            service.updatePropertyNameOrValues(repo, model, now, userUuid);
            transaction.commit();
            repo.getSession().clear();
        }
        return service.getProperty(repo, propertyUuid).getRanges().get(0).getId();
    }
}
