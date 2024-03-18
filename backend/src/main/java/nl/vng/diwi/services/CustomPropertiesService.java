package nl.vng.diwi.services;

import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.CustomCategoryValue;
import nl.vng.diwi.dal.entities.CustomCategoryValueState;
import nl.vng.diwi.dal.entities.CustomOrdinalValue;
import nl.vng.diwi.dal.entities.CustomOrdinalValueState;
import nl.vng.diwi.dal.entities.CustomProperty;
import nl.vng.diwi.dal.entities.CustomPropertyState;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.dal.entities.enums.PropertyType;
import nl.vng.diwi.dal.entities.superclasses.ChangeDataSuperclass;
import nl.vng.diwi.models.CustomPropertyModel;
import nl.vng.diwi.models.OrdinalSelectDisabledModel;
import nl.vng.diwi.models.SelectDisabledModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CustomPropertiesService {

    public CustomPropertiesService() {
    }

    public CustomPropertyModel getCustomProperty(VngRepository repo, UUID customPropertyUuid) {
        return repo.getCustomPropertiesDAO().getCustomProperyById(customPropertyUuid);
    }

    public List<CustomPropertyModel> getAllCustomProperties(VngRepository repo, ObjectType objectType) {
        return repo.getCustomPropertiesDAO().getCustomProperiesList(objectType);
    }

    public UUID createCustomProperty(VngRepository repo, CustomPropertyModel customPropertyModel, ZonedDateTime createTime, UUID loggedUserUuid) {

        CustomProperty customProperty = new CustomProperty();
        repo.persist(customProperty);

        CustomPropertyState customPropertyState = new CustomPropertyState();
        customPropertyState.setCustomProperty(customProperty);
        customPropertyState.setPropertyName(customPropertyModel.getName());
        customPropertyState.setObjectType(customPropertyModel.getObjectType());
        customPropertyState.setPropertyType(customPropertyModel.getPropertyType());
        customPropertyState.setChangeStartDate(createTime);
        customPropertyState.setCreateUser(repo.findById(User.class, loggedUserUuid));
        repo.persist(customPropertyState);

        if (PropertyType.CATEGORY.equals(customPropertyModel.getPropertyType())) {
            if (customPropertyModel.getCategoryValues() != null) {
                customPropertyModel.getCategoryValues().forEach(cat -> {
                    CustomCategoryValue customCategoryValue = new CustomCategoryValue();
                    customCategoryValue.setCustomProperty(customProperty);
                    repo.persist(customCategoryValue);
                    CustomCategoryValueState customCategoryValueState = new CustomCategoryValueState();
                    customCategoryValueState.setCustomCategoryValue(customCategoryValue);
                    customCategoryValueState.setLabel(cat.getName());
                    customCategoryValueState.setChangeStartDate(createTime);
                    customCategoryValueState.setCreateUser(repo.getReferenceById(User.class, loggedUserUuid));
                    repo.persist(customCategoryValueState);
                });
            }
        } else if (PropertyType.ORDINAL.equals(customPropertyModel.getPropertyType())) {
            if (customPropertyModel.getOrdinalValues() != null) {
                customPropertyModel.getOrdinalValues().forEach(ord -> {
                    CustomOrdinalValue customOrdinalValue = new CustomOrdinalValue();
                    customOrdinalValue.setCustomProperty(customProperty);
                    repo.persist(customOrdinalValue);
                    CustomOrdinalValueState customOrdinalValueState = new CustomOrdinalValueState();
                    customOrdinalValueState.setCustomOrdinalValue(customOrdinalValue);
                    customOrdinalValueState.setLabel(ord.getName());
                    customOrdinalValueState.setOrdinalLevel(ord.getLevel());
                    customOrdinalValueState.setChangeStartDate(createTime);
                    customOrdinalValueState.setCreateUser(repo.getReferenceById(User.class, loggedUserUuid));
                    repo.persist(customOrdinalValueState);
                });
            }
        }

        return customProperty.getId();
    }

    public void updateCustomPropertyNameOrValues(VngRepository repo, CustomPropertyModel customPropertyModel, ZonedDateTime now, UUID loggedUserUuid)
        throws VngNotFoundException, VngBadRequestException {

        CustomProperty customProperty = repo.findById(CustomProperty.class, customPropertyModel.getId());

        if (customProperty == null || customProperty.getStates() == null || customProperty.getStates().isEmpty()) {
            throw new VngNotFoundException("Custom property could not be found.");
        }

        //TODO: check uniqueness of name + propertyType + objectType ??

        List<CustomPropertyState> statesList = customProperty.getStates();
        statesList.sort(Comparator.comparing(CustomPropertyState::getChangeStartDate).reversed());

        CustomPropertyState state = statesList.get(0);

        User userReference = repo.getReferenceById(User.class, loggedUserUuid);

        if (!state.getPropertyName().equals(customPropertyModel.getName())) {
            CustomPropertyState newState = new CustomPropertyState();
            newState.setCustomProperty(customProperty);
            newState.setPropertyName(customPropertyModel.getName());
            newState.setObjectType(state.getObjectType());
            newState.setPropertyType(state.getPropertyType());
            newState.setChangeStartDate(now);
            newState.setCreateUser(userReference);
            repo.persist(newState);

            state.setChangeEndDate(now);
            state.setChangeUser(userReference);
            repo.persist(state);
        }

        if (PropertyType.CATEGORY.equals(state.getPropertyType()) && customPropertyModel.getCategoryValues() != null) {
            List<CustomCategoryValue> categoryValues = customProperty.getCategoryValues();

            for (SelectDisabledModel catValueModel : customPropertyModel.getCategoryValues()) {
                if (catValueModel.getId() == null) { //new category value TODO: check label does not already exist in that category?
                    CustomCategoryValue newCat = new CustomCategoryValue();
                    newCat.setCustomProperty(customProperty);
                    repo.persist(newCat);
                    CustomCategoryValueState newCatState = new CustomCategoryValueState();
                    newCatState.setCustomCategoryValue(newCat);
                    newCatState.setLabel(catValueModel.getName());
                    newCatState.setCreateUser(userReference);
                    newCatState.setChangeStartDate(now);
                    if (catValueModel.getDisabled() == Boolean.TRUE) {
                        newCatState.setChangeEndDate(now);
                        newCatState.setChangeUser(userReference);
                    }
                    repo.persist(newCatState);
                } else { //update existing category value: disable/re-enable and/or update label
                    CustomCategoryValue categoryValue = categoryValues.stream().filter(cv -> cv.getId().equals(catValueModel.getId())).findFirst()
                        .orElseThrow(() -> new VngBadRequestException("Provided id of category does not match any known categories."));
                    CustomCategoryValueState categoryValueState = Collections.max(categoryValue.getStates(), Comparator.comparing(ChangeDataSuperclass::getChangeStartDate));

                    boolean updateName = !Objects.equals(catValueModel.getName(), categoryValueState.getLabel());
                    boolean disableCatValue = (catValueModel.getDisabled()) && (categoryValueState.getChangeEndDate() == null);
                    boolean enableCatValue = (!catValueModel.getDisabled()) && (categoryValueState.getChangeEndDate() != null);

                    if (disableCatValue || updateName || enableCatValue) {
                        categoryValueState.setChangeUser(userReference);
                        categoryValueState.setChangeEndDate(now);
                        repo.persist(categoryValueState);
                        if (updateName || enableCatValue) {
                            CustomCategoryValueState newCategoryValueState = new CustomCategoryValueState();
                            newCategoryValueState.setCustomCategoryValue(categoryValue);
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
            List<CustomOrdinalValue> ordinalValues = customProperty.getOrdinalValues();

            for (OrdinalSelectDisabledModel ordValueModel : customPropertyModel.getOrdinalValues()) {
                if (ordValueModel.getId() == null) { //new category value TODO: check label does not already exist in that ordinal list?
                    CustomOrdinalValue newOrd = new CustomOrdinalValue();
                    newOrd.setCustomProperty(customProperty);
                    repo.persist(newOrd);
                    CustomOrdinalValueState newOrdState = new CustomOrdinalValueState();
                    newOrdState.setCustomOrdinalValue(newOrd);
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
                    CustomOrdinalValue ordinalValue = ordinalValues.stream().filter(cv -> cv.getId().equals(ordValueModel.getId())).findFirst()
                        .orElseThrow(() -> new VngBadRequestException("Provided id of category does not match any known categories."));
                    CustomOrdinalValueState ordinalValueState = Collections.max(ordinalValue.getStates(), Comparator.comparing(ChangeDataSuperclass::getChangeStartDate));

                    boolean updateNameOrLevel = !Objects.equals(ordValueModel.getName(), ordinalValueState.getLabel()) ||
                        !Objects.equals(ordValueModel.getLevel(), ordinalValueState.getOrdinalLevel());
                    boolean disableCatValue = (ordValueModel.getDisabled()) && (ordinalValueState.getChangeEndDate() == null);
                    boolean enableCatValue = (!ordValueModel.getDisabled()) && (ordinalValueState.getChangeEndDate() != null);

                    if (disableCatValue || updateNameOrLevel || enableCatValue) {
                        ordinalValueState.setChangeUser(userReference);
                        ordinalValueState.setChangeEndDate(now);
                        repo.persist(ordinalValueState);
                        if (updateNameOrLevel || enableCatValue) {
                            CustomOrdinalValueState newOrdinalValueState = new CustomOrdinalValueState();
                            newOrdinalValueState.setCustomOrdinalValue(ordinalValue);
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

    }

    public void disableCustomProperty(VngRepository repo, UUID customPropertyUuid, ZonedDateTime now, UUID loggedUserUuid)
        throws VngNotFoundException {

        CustomProperty customProperty = repo.findById(CustomProperty.class, customPropertyUuid);

        if (customProperty == null || customProperty.getStates() == null || customProperty.getStates().isEmpty()) {
            throw new VngNotFoundException("Custom property could not be found.");
        }

        List<CustomPropertyState> statesList = customProperty.getStates();
        statesList.sort(Comparator.comparing(CustomPropertyState::getChangeStartDate).reversed());

        CustomPropertyState state = statesList.get(0);

        state.setChangeEndDate(now);
        state.setChangeUser(repo.getReferenceById(User.class, loggedUserUuid));
        repo.persist(state);

        //TODO: check if this property was being used in a project / houseblock? - no, for now
        //TODO: also set changeEndDate for the project / houseblock changelog using this? - no, for now
        //TODO: do not allow disabling in this case and return a warning? - no, for now

    }

}
