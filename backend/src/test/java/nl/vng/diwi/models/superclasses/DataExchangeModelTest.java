package nl.vng.diwi.models.superclasses;

import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.models.DataExchangeModel;
import nl.vng.diwi.models.DataExchangeOptionModel;
import nl.vng.diwi.models.DataExchangePropertyModel;
import nl.vng.diwi.models.OrdinalSelectDisabledModel;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.dal.entities.enums.PropertyType;
import nl.vng.diwi.models.SelectDisabledModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DataExchangeModelTest {

    private DataExchangeModel dataExchangeModel;
    private List<PropertyModel> propertyModels;

    @BeforeEach
    public void setUp() {
        dataExchangeModel = new DataExchangeModel();
        propertyModels = new ArrayList<>();
    }

    @Test
    public void testValidateDxProperties_ObjectTypeMismatch() {
        UUID propId = UUID.randomUUID();
        PropertyModel prop = new PropertyModel();
        prop.setId(propId);
        prop.setObjectType(ObjectType.PROJECT);

        DataExchangePropertyModel dxProp = new DataExchangePropertyModel();
        dxProp.setCustomPropertyId(propId);
        dxProp.setName("Test Property");
        dxProp.setObjectType(ObjectType.WONINGBLOK);

        propertyModels.add(prop);
        dataExchangeModel.setProperties(List.of(dxProp));

        String result = dataExchangeModel.validateDxProperties(propertyModels);
        assertEquals("The selected property for 'Test Property' does not match the expected object type " + "(" + dxProp.getObjectType() + ")" + ".", result);
    }

    @Test
    public void testValidateDxProperties_MandatoryFlagMismatch() {
        UUID propId = UUID.randomUUID();
        PropertyModel prop = new PropertyModel();
        prop.setId(propId);
        prop.setMandatory(false);
        prop.setObjectType(ObjectType.PROJECT);
        prop.setPropertyType(PropertyType.CATEGORY);

        DataExchangePropertyModel dxProp = new DataExchangePropertyModel();
        dxProp.setCustomPropertyId(propId);
        dxProp.setName("Test Property");
        dxProp.setMandatory(true);
        dxProp.setObjectType(ObjectType.PROJECT);
        dxProp.setPropertyTypes(List.of(PropertyType.CATEGORY));

        propertyModels.add(prop);
        dataExchangeModel.setProperties(List.of(dxProp));

        String result = dataExchangeModel.validateDxProperties(propertyModels);
        assertEquals("The selected property for 'Test Property' does not have the expected mandatory flag (true).", result);
    }

    @Test
    public void testValidateDxProperties_SingleSelectFlagMismatch() {
        UUID propId = UUID.randomUUID();
        PropertyModel prop = new PropertyModel();
        prop.setId(propId);
        prop.setPropertyType(PropertyType.CATEGORY);
        prop.setSingleSelect(false);
        prop.setMandatory(false);

        DataExchangePropertyModel dxProp = new DataExchangePropertyModel();
        dxProp.setCustomPropertyId(propId);
        dxProp.setName("Test Property");
        dxProp.setPropertyTypes(List.of(PropertyType.CATEGORY));
        dxProp.setSingleSelect(true);
        dxProp.setMandatory(false);

        propertyModels.add(prop);
        dataExchangeModel.setProperties(List.of(dxProp));

        String result = dataExchangeModel.validateDxProperties(propertyModels);
        assertEquals("The selected property for 'Test Property' does not have the expected single select flag.", result);
    }

    @Test
    public void testValidateDxProperties_CategoryValueIdsMismatch() {
        UUID propId = UUID.randomUUID();
        PropertyModel prop = new PropertyModel();
        prop.setId(propId);
        prop.setPropertyType(PropertyType.CATEGORY);
        prop.setSingleSelect(false);

        SelectDisabledModel category = new SelectDisabledModel();
        category.setId(UUID.randomUUID());
        category.setDisabled(false);
        prop.setCategories(List.of(category));

        DataExchangePropertyModel dxProp = new DataExchangePropertyModel();
        dxProp.setCustomPropertyId(propId);
        dxProp.setName("Test Property");
        dxProp.setPropertyTypes(List.of(PropertyType.CATEGORY));
        dxProp.setMandatory(false);
        dxProp.setSingleSelect(false);

        DataExchangeOptionModel option = new DataExchangeOptionModel();
        option.setPropertyCategoryValueIds(List.of(UUID.randomUUID())); // Mismatched ID
        dxProp.setOptions(List.of(option));

        propertyModels.add(prop);
        dataExchangeModel.setProperties(List.of(dxProp));

        String result = dataExchangeModel.validateDxProperties(propertyModels);
        assertEquals("The selected property for 'Test Property' category value ids are not valid options for custom property " + prop.getId(), result);
    }

    @Test
    public void testValidateDxProperties_OrdinalValueIdsMismatch() {
        UUID propId = UUID.randomUUID();
        PropertyModel prop = new PropertyModel();
        prop.setId(propId);
        prop.setPropertyType(PropertyType.ORDINAL);
        prop.setMandatory(false);
        prop.setSingleSelect(false);

        OrdinalSelectDisabledModel ordinal = new OrdinalSelectDisabledModel();
        ordinal.setId(UUID.randomUUID());
        ordinal.setDisabled(false);
        prop.setOrdinals(List.of(ordinal));

        DataExchangePropertyModel dxProp = new DataExchangePropertyModel();
        dxProp.setCustomPropertyId(propId);
        dxProp.setName("Test Property");
        dxProp.setPropertyTypes(List.of(PropertyType.ORDINAL));
        dxProp.setMandatory(false);
        dxProp.setSingleSelect(false);

        DataExchangeOptionModel option = new DataExchangeOptionModel();
        option.setPropertyOrdinalValueIds(List.of(UUID.randomUUID())); // Mismatched ID
        dxProp.setOptions(List.of(option));

        propertyModels.add(prop);
        dataExchangeModel.setProperties(List.of(dxProp));

        String result = dataExchangeModel.validateDxProperties(propertyModels);
        assertEquals("The selected property for 'Test Property' ordinal value ids are not valid options for custom property " + prop.getId(), result);
    }

    @Test
    public void testValidateDxProperties_ValidProperties() {
        UUID propId = UUID.randomUUID();
        PropertyModel prop = new PropertyModel();
        prop.setId(propId);
        prop.setObjectType(ObjectType.PROJECT);
        prop.setPropertyType(PropertyType.CATEGORY);
        prop.setMandatory(true);
        prop.setSingleSelect(true);

        SelectDisabledModel category = new SelectDisabledModel();
        category.setId(UUID.randomUUID());
        category.setDisabled(false);
        prop.setCategories(List.of(category));

        DataExchangePropertyModel dxProp = new DataExchangePropertyModel();
        dxProp.setCustomPropertyId(propId);
        dxProp.setName("Test Property");
        dxProp.setObjectType(ObjectType.PROJECT);
        dxProp.setPropertyTypes(List.of(PropertyType.CATEGORY));
        dxProp.setMandatory(true);
        dxProp.setSingleSelect(true);

        DataExchangeOptionModel option = new DataExchangeOptionModel();
        option.setPropertyCategoryValueIds(List.of(category.getId()));
        dxProp.setOptions(List.of(option));

        propertyModels.add(prop);
        dataExchangeModel.setProperties(List.of(dxProp));

        String result = dataExchangeModel.validateDxProperties(propertyModels);
        assertNull(result);
    }
}
