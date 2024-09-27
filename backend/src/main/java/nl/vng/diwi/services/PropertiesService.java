package nl.vng.diwi.services;

import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.PropertyCategoryValue;
import nl.vng.diwi.dal.entities.PropertyCategoryValueState;
import nl.vng.diwi.dal.entities.PropertyOrdinalValue;
import nl.vng.diwi.dal.entities.PropertyOrdinalValueState;
import nl.vng.diwi.dal.entities.Property;
import nl.vng.diwi.dal.entities.PropertyRangeCategoryValue;
import nl.vng.diwi.dal.entities.PropertyRangeCategoryValueState;
import nl.vng.diwi.dal.entities.PropertyState;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.dal.entities.enums.PropertyKind;
import nl.vng.diwi.dal.entities.enums.PropertyType;
import nl.vng.diwi.dal.entities.superclasses.ChangeDataSuperclass;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.models.OrdinalSelectDisabledModel;
import nl.vng.diwi.models.RangeSelectDisabledModel;
import nl.vng.diwi.models.SelectDisabledModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PropertiesService {

    public PropertiesService() {
    }

    public PropertyModel getProperty(VngRepository repo, UUID propertyUuid) {
        return repo.getPropertyDAO().getPropertyById(propertyUuid);
    }

    public UUID getPropertyUuid(VngRepository repo, String propertyName) {
        PropertyState state = repo.getPropertyDAO().getActivePropertyStateByName(propertyName);
        if (state != null) {
            return state.getProperty().getId();
        }
        return null;
    }

    public List<PropertyModel> getAllProperties(VngRepository repo, ObjectType objectType, Boolean disabled, PropertyKind type) {
        return repo.getPropertyDAO().getPropertiesList(objectType, disabled, type);
    }

    public boolean checkPropertyNameExists(VngRepository repo, String name, UUID currentPropertyUuid) {
        PropertyState state = repo.getPropertyDAO().getActivePropertyStateByName(name);
        if (state != null && !state.getProperty().getId().equals(currentPropertyUuid)) {
            return true;
        }
        return false;
    }

    public List<PropertyCategoryValueState> getCategoryStatesByPropertyName(VngRepository repo, String propertyName) {
        return repo.getPropertyDAO().getCategoryStatesByPropertyName(propertyName);
    }

    public UUID createCustomProperty(VngRepository repo, PropertyModel propertyModel, ZonedDateTime createTime, UUID loggedUserUuid)
        throws VngBadRequestException {

        if (checkPropertyNameExists(repo, propertyModel.getName(), null)) {
            throw new VngBadRequestException("Property name already exists");
        }

        Property property = new Property();
        property.setType(PropertyKind.CUSTOM);
        repo.persist(property);

        PropertyState propertyState = new PropertyState();
        propertyState.setProperty(property);
        propertyState.setPropertyName(propertyModel.getName());
        propertyState.setObjectType(propertyModel.getObjectType());
        propertyState.setPropertyType(propertyModel.getPropertyType());
        propertyState.setChangeStartDate(createTime);
        propertyState.setCreateUser(repo.findById(User.class, loggedUserUuid));
        repo.persist(propertyState);

        if (PropertyType.CATEGORY.equals(propertyModel.getPropertyType())) {
            if (propertyModel.getCategories() != null) {
                propertyModel.getCategories().forEach(cat -> {
                    PropertyCategoryValue categoryValue = new PropertyCategoryValue();
                    categoryValue.setProperty(property);
                    repo.persist(categoryValue);
                    PropertyCategoryValueState categoryValueState = new PropertyCategoryValueState();
                    categoryValueState.setCategoryValue(categoryValue);
                    categoryValueState.setLabel(cat.getName());
                    categoryValueState.setChangeStartDate(createTime);
                    categoryValueState.setCreateUser(repo.getReferenceById(User.class, loggedUserUuid));
                    repo.persist(categoryValueState);
                });
            }
        } else if (PropertyType.ORDINAL.equals(propertyModel.getPropertyType())) {
            if (propertyModel.getOrdinals() != null) {
                propertyModel.getOrdinals().forEach(ord -> {
                    PropertyOrdinalValue propertyOrdinalValue = new PropertyOrdinalValue();
                    propertyOrdinalValue.setProperty(property);
                    repo.persist(propertyOrdinalValue);
                    PropertyOrdinalValueState propertyOrdinalValueState = new PropertyOrdinalValueState();
                    propertyOrdinalValueState.setPropertyOrdinalValue(propertyOrdinalValue);
                    propertyOrdinalValueState.setLabel(ord.getName());
                    propertyOrdinalValueState.setOrdinalLevel(ord.getLevel());
                    propertyOrdinalValueState.setChangeStartDate(createTime);
                    propertyOrdinalValueState.setCreateUser(repo.getReferenceById(User.class, loggedUserUuid));
                    repo.persist(propertyOrdinalValueState);
                });
            }
        }

        return property.getId();
    }

    public void updatePropertyNameOrValues(VngRepository repo, PropertyModel propertyModel, ZonedDateTime now, UUID loggedUserUuid)
        throws VngNotFoundException, VngBadRequestException {

        Property property = repo.findById(Property.class, propertyModel.getId());

        if (property == null || property.getStates() == null || property.getStates().isEmpty()) {
            throw new VngNotFoundException("Property could not be found.");
        }

        if (checkPropertyNameExists(repo, propertyModel.getName(), property.getId())) {
            throw new VngBadRequestException("Custom property name already exists");
        }

        List<PropertyState> statesList = property.getStates();
        statesList.sort(Comparator.comparing(PropertyState::getChangeStartDate).reversed());

        PropertyState state = statesList.get(0);

        User userReference = repo.getReferenceById(User.class, loggedUserUuid);

        if (!state.getPropertyName().equals(propertyModel.getName())) {
            if (property.getType() != PropertyKind.CUSTOM) {
                throw new VngNotFoundException("Only custom properties can have the name updated.");
            }
            PropertyState newState = new PropertyState();
            newState.setProperty(property);
            newState.setPropertyName(propertyModel.getName());
            newState.setObjectType(state.getObjectType());
            newState.setPropertyType(state.getPropertyType());
            newState.setChangeStartDate(now);
            newState.setCreateUser(userReference);
            repo.persist(newState);

            state.setChangeEndDate(now);
            state.setChangeUser(userReference);
            repo.persist(state);
        }

        if (PropertyType.CATEGORY.equals(state.getPropertyType()) && propertyModel.getCategories() != null) {
            List<PropertyCategoryValue> categoryValues = property.getCategoryValues();

            for (SelectDisabledModel catValueModel : propertyModel.getCategories()) {
                if (catValueModel.getId() == null) { //new category value TODO: check label does not already exist in that category?
                    PropertyCategoryValue newCat = new PropertyCategoryValue();
                    newCat.setProperty(property);
                    repo.persist(newCat);
                    PropertyCategoryValueState newCatState = new PropertyCategoryValueState();
                    newCatState.setCategoryValue(newCat);
                    newCatState.setLabel(catValueModel.getName());
                    newCatState.setCreateUser(userReference);
                    newCatState.setChangeStartDate(now);
                    if (catValueModel.getDisabled() == Boolean.TRUE) {
                        newCatState.setChangeEndDate(now);
                        newCatState.setChangeUser(userReference);
                    }
                    repo.persist(newCatState);
                } else { //update existing category value: disable/re-enable and/or update label
                    PropertyCategoryValue categoryValue = categoryValues.stream().filter(cv -> cv.getId().equals(catValueModel.getId())).findFirst()
                        .orElseThrow(() -> new VngBadRequestException("Provided id of category does not match any known categories."));
                    PropertyCategoryValueState categoryValueState = Collections.max(categoryValue.getStates(), Comparator.comparing(ChangeDataSuperclass::getChangeStartDate));

                    boolean updateName = !Objects.equals(catValueModel.getName(), categoryValueState.getLabel());
                    boolean disableCatValue = (catValueModel.getDisabled()) && (categoryValueState.getChangeEndDate() == null);
                    boolean enableCatValue = (!catValueModel.getDisabled()) && (categoryValueState.getChangeEndDate() != null);

                    if (disableCatValue || updateName || enableCatValue) {
                        categoryValueState.setChangeUser(userReference);
                        categoryValueState.setChangeEndDate(now);
                        repo.persist(categoryValueState);
                        if (updateName || enableCatValue) {
                            PropertyCategoryValueState newCategoryValueState = new PropertyCategoryValueState();
                            newCategoryValueState.setCategoryValue(categoryValue);
                            newCategoryValueState.setLabel(catValueModel.getName());
                            newCategoryValueState.setCreateUser(userReference);
                            newCategoryValueState.setChangeStartDate(now);
                            if (catValueModel.getDisabled()) {
                                newCategoryValueState.setChangeUser(userReference);
                                newCategoryValueState.setChangeEndDate(now);
                            }
                            repo.persist(newCategoryValueState);
                        }
                    }
                }
            }
        }

        if (PropertyType.ORDINAL.equals(state.getPropertyType())) {
            List<PropertyOrdinalValue> ordinalValues = property.getOrdinalValues();

            for (OrdinalSelectDisabledModel ordValueModel : propertyModel.getOrdinals()) {
                if (ordValueModel.getId() == null) { //new category value TODO: check label does not already exist in that ordinal list?
                    PropertyOrdinalValue newOrd = new PropertyOrdinalValue();
                    newOrd.setProperty(property);
                    repo.persist(newOrd);
                    PropertyOrdinalValueState newOrdState = new PropertyOrdinalValueState();
                    newOrdState.setPropertyOrdinalValue(newOrd);
                    newOrdState.setLabel(ordValueModel.getName());
                    newOrdState.setOrdinalLevel(ordValueModel.getLevel());
                    newOrdState.setCreateUser(userReference);
                    newOrdState.setChangeStartDate(now);
                    if (ordValueModel.getDisabled() == Boolean.TRUE) {
                        newOrdState.setChangeEndDate(now);
                        newOrdState.setChangeUser(userReference);
                    }
                    repo.persist(newOrdState);
                } else { //update existing category value: disable/re-enable and/or update label
                    PropertyOrdinalValue ordinalValue = ordinalValues.stream().filter(cv -> cv.getId().equals(ordValueModel.getId())).findFirst()
                        .orElseThrow(() -> new VngBadRequestException("Provided id of category does not match any known categories."));
                    PropertyOrdinalValueState ordinalValueState = Collections.max(ordinalValue.getStates(), Comparator.comparing(ChangeDataSuperclass::getChangeStartDate));

                    boolean updateNameOrLevel = !Objects.equals(ordValueModel.getName(), ordinalValueState.getLabel()) ||
                        !Objects.equals(ordValueModel.getLevel(), ordinalValueState.getOrdinalLevel());
                    boolean disableCatValue = (ordValueModel.getDisabled()) && (ordinalValueState.getChangeEndDate() == null);
                    boolean enableCatValue = (!ordValueModel.getDisabled()) && (ordinalValueState.getChangeEndDate() != null);

                    if (disableCatValue || updateNameOrLevel || enableCatValue) {
                        ordinalValueState.setChangeUser(userReference);
                        ordinalValueState.setChangeEndDate(now);
                        repo.persist(ordinalValueState);
                        if (updateNameOrLevel || enableCatValue) {
                            PropertyOrdinalValueState newOrdinalValueState = new PropertyOrdinalValueState();
                            newOrdinalValueState.setPropertyOrdinalValue(ordinalValue);
                            newOrdinalValueState.setLabel(ordValueModel.getName());
                            newOrdinalValueState.setOrdinalLevel(ordValueModel.getLevel());
                            newOrdinalValueState.setCreateUser(userReference);
                            newOrdinalValueState.setChangeStartDate(now);
                            if (ordValueModel.getDisabled()) {
                                newOrdinalValueState.setChangeUser(userReference);
                                newOrdinalValueState.setChangeEndDate(now);
                            }
                            repo.persist(newOrdinalValueState);
                        }
                    }
                }
            }
        }

        if (PropertyType.RANGE_CATEGORY.equals(state.getPropertyType()) && propertyModel.getRanges() != null) {
            List<PropertyRangeCategoryValue> rangeValues = property.getRangeValues();

            for (RangeSelectDisabledModel rangeValueModel : propertyModel.getRanges()) {
                if (rangeValueModel.getId() == null) { //new category value TODO: check label does not already exist in that category?
                    PropertyRangeCategoryValue newCat = new PropertyRangeCategoryValue();
                    newCat.setProperty(property);
                    repo.persist(newCat);
                    PropertyRangeCategoryValueState newCatState = new PropertyRangeCategoryValueState();
                    newCatState.setRangeCategoryValue(newCat);
                    newCatState.setName(rangeValueModel.getName());
                    newCatState.setMin(rangeValueModel.getMin().longValueExact());
                    newCatState.setMax(rangeValueModel.getMax().longValueExact());
                    newCatState.setCreateUser(userReference);
                    newCatState.setChangeStartDate(now);
                    if (rangeValueModel.getDisabled() == Boolean.TRUE) {
                        newCatState.setChangeEndDate(now);
                        newCatState.setChangeUser(userReference);
                    }
                    repo.persist(newCatState);
                } else { //update existing category value: disable/re-enable and/or update label / min / max
                    PropertyRangeCategoryValue rangeValue = rangeValues.stream().filter(cv -> cv.getId().equals(rangeValueModel.getId())).findFirst()
                        .orElseThrow(() -> new VngBadRequestException("Provided id of category does not match any known categories."));
                    PropertyRangeCategoryValueState rangeValueState = Collections.max(rangeValue.getStates(), Comparator.comparing(ChangeDataSuperclass::getChangeStartDate));

                    boolean updateNameMinMax = !Objects.equals(rangeValueModel.getName(), rangeValueState.getName()) ||
                        !Objects.equals(rangeValueModel.getMax(), rangeValueState.getMax()) || !Objects.equals(rangeValueModel.getMin(), rangeValueState.getMin());
                    boolean disableCatValue = (rangeValueModel.getDisabled()) && (rangeValueState.getChangeEndDate() == null);
                    boolean enableCatValue = (!rangeValueModel.getDisabled()) && (rangeValueState.getChangeEndDate() != null);

                    if (disableCatValue || updateNameMinMax || enableCatValue) {
                        rangeValueState.setChangeUser(userReference);
                        rangeValueState.setChangeEndDate(now);
                        repo.persist(rangeValueState);
                        if (updateNameMinMax || enableCatValue) {
                            PropertyRangeCategoryValueState newRangeValueState = new PropertyRangeCategoryValueState();
                            newRangeValueState.setRangeCategoryValue(rangeValue);
                            newRangeValueState.setName(rangeValueModel.getName());
                            newRangeValueState.setMax(rangeValueModel.getMax().longValueExact());
                            newRangeValueState.setMin(rangeValueModel.getMin().longValueExact());
                            newRangeValueState.setCreateUser(userReference);
                            newRangeValueState.setChangeStartDate(now);
                            if (rangeValueModel.getDisabled()) {
                                newRangeValueState.setChangeUser(userReference);
                                newRangeValueState.setChangeEndDate(now);
                            }
                            repo.persist(newRangeValueState);
                        }
                    }
                }
            }
        }
    }

    public void disableCustomProperty(VngRepository repo, UUID customPropertyUuid, ZonedDateTime now, UUID loggedUserUuid)
        throws VngNotFoundException {

        Property property = repo.findById(Property.class, customPropertyUuid);

        if (property == null || property.getStates() == null || property.getStates().isEmpty()) {
            throw new VngNotFoundException("Property could not be found.");
        }

        if (property.getType() != PropertyKind.CUSTOM) {
            throw new VngNotFoundException("Only custom properties can be disabled.");
        }

        List<PropertyState> statesList = property.getStates();
        statesList.sort(Comparator.comparing(PropertyState::getChangeStartDate).reversed());

        PropertyState state = statesList.get(0);

        state.setChangeEndDate(now);
        state.setChangeUser(repo.getReferenceById(User.class, loggedUserUuid));
        repo.persist(state);

        //TODO: check if this property was being used in a project / houseblock? - no, for now
        //TODO: also set changeEndDate for the project / houseblock changelog using this? - no, for now
        //TODO: do not allow disabling in this case and return a warning? - no, for now

    }

}
