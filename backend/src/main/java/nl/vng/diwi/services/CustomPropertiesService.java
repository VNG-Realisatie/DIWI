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
import nl.vng.diwi.models.CustomPropertyModel;
import nl.vng.diwi.rest.VngNotFoundException;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
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
            CustomCategoryValue customCategoryValue = new CustomCategoryValue();
            customCategoryValue.setCustomProperty(customProperty);
            repo.persist(customCategoryValue);

            if (customPropertyModel.getCategoryValues() != null) {
                customPropertyModel.getCategoryValues().forEach(cat -> {
                    CustomCategoryValueState customCategoryValueState = new CustomCategoryValueState();
                    customCategoryValueState.setCustomCategoryValue(customCategoryValue);
                    customCategoryValueState.setLabel(cat.getName());
                    customCategoryValueState.setChangeStartDate(createTime);
                    customCategoryValueState.setCreateUser(repo.getReferenceById(User.class, loggedUserUuid));
                    repo.persist(customCategoryValueState);
                });
            }
        } else if (PropertyType.ORDINAL.equals(customPropertyModel.getPropertyType())) {
            CustomOrdinalValue customOrdinalValue = new CustomOrdinalValue();
            customOrdinalValue.setCustomProperty(customProperty);
            repo.persist(customOrdinalValue);

            if (customPropertyModel.getOrdinalValues() != null) {
                customPropertyModel.getOrdinalValues().forEach(ord -> {
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
        throws VngNotFoundException {

        CustomProperty customProperty = repo.findById(CustomProperty.class, customPropertyModel.getId());

        if (customProperty == null || customProperty.getStates() == null || customProperty.getStates().isEmpty()) {
            throw new VngNotFoundException("Custom property could not be found.");
        }

        //TODO: check uniqueness of name + propertyType + objectType ??

        List<CustomPropertyState> statesList = customProperty.getStates();
        statesList.sort(Comparator.comparing(CustomPropertyState::getChangeStartDate).reversed());

        CustomPropertyState state = statesList.get(0);

        if (!state.getPropertyName().equals(customPropertyModel.getName())) {
            CustomPropertyState newState = new CustomPropertyState();
            newState.setCustomProperty(customProperty);
            newState.setPropertyName(customPropertyModel.getName());
            newState.setObjectType(state.getObjectType());
            newState.setPropertyType(state.getPropertyType());
            newState.setChangeStartDate(now);
            newState.setCreateUser(repo.getReferenceById(User.class, loggedUserUuid));
            repo.persist(newState);

            state.setChangeEndDate(now);
            state.setChangeUser(repo.getReferenceById(User.class, loggedUserUuid));
            repo.persist(state);
        }

        if (PropertyType.CATEGORY.equals(state.getPropertyType())) {
//            TODO: compare model categories with DB categories and update CustomCategoryValue & CustomCategoryValueState
            //TODO: clarify how exactly is the DIFF done => now, the frontend has all values and disabled flag for each option
            // frontend should know if they want to re-enable an old one, or to create a new one, or disable an existing one.
        }

        if (PropertyType.ORDINAL.equals(state.getPropertyType())) {
//            TODO: compare model categories with DB categories and update CustomOrdinalValue & CustomOrdinalValueState
            //TODO: clarify how exactly is the DIFF done => now, the frontend has all values and disabled flag for each option
            // frontend should know if they want to re-enable an old one, or to create a new one, or disable an existing one, or change ordinalLevel
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
