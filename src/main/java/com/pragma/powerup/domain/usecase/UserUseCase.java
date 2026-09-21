package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IRestaurantValidationPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.regex.Pattern;

public class UserUseCase implements IUserServicePort {

    private static final Long ROLE_PROPRIETARY =2L;
    private static final Long ROLE_EMPLOYEE_ID =3L;
    private static final Long ROLE_CLIENT_ID =4L;
    private static final int MIN_AGE=18;
    private static final ZoneId ZONE_ID= ZoneId.of("America/Bogota");
    private static final Pattern emainPattern= Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern cellphonePattern = Pattern.compile("^\\+?\\d{1,13}$");
    private static final Pattern documentPattern=Pattern.compile("^\\d+$");

    private final IUserPersistencePort userPersistencePort;
    private final PasswordEncoder passwordEncoder;
    private final IRestaurantValidationPort restaurantValidationPort;

    public UserUseCase(IUserPersistencePort userPersistencePort, PasswordEncoder passwordEncoder,IRestaurantValidationPort restaurantValidationPort) {
        this.userPersistencePort = userPersistencePort;
        this.passwordEncoder = passwordEncoder;
        this.restaurantValidationPort=restaurantValidationPort;
    }

    @Override
    public UserModel getUserById(Long id) {
        return userPersistencePort.getUserById(id);
    }

    @Override
    public void saveClient(UserModel userModel) {
        validateEmail(userModel.getEmail());
        validateAge(userModel.getBirthDate());
        validateCellphone(userModel.getCellphone());
        validateDocument(userModel.getDocument());

        if(userPersistencePort.existsByEmail(userModel.getEmail())){
            throw new InvalidEmailDuplicate();
        }
        userModel.setIdRole(ROLE_CLIENT_ID);
        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        userPersistencePort.saveUser(userModel);
    }

    @Override
    public void saveOwer(UserModel userModel) {
        validateEmail(userModel.getEmail());
        validateAge(userModel.getBirthDate());
        validateCellphone(userModel.getCellphone());
        validateDocument(userModel.getDocument());

        if(userPersistencePort.existsByEmail(userModel.getEmail())){
            throw new InvalidEmailDuplicate();
        }
        userModel.setIdRole(ROLE_PROPRIETARY);
        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        userPersistencePort.saveUser(userModel);
    }

    @Override
    public void saveEmployee(UserModel userModel, Long idOwner) {
        validateEmail(userModel.getEmail());
        validateAge(userModel.getBirthDate());
        validateCellphone(userModel.getCellphone());
        validateDocument(userModel.getDocument());

        if(userPersistencePort.existsByEmail(userModel.getEmail())){
            throw new InvalidEmailDuplicate();
        }

        Long restaurantOwnerId= restaurantValidationPort.getRestaurantOwnerId(userModel.getIdRestaurant());
        if(!restaurantOwnerId.equals(idOwner)){
            throw new UserNotOwnerRestaurantException();
        }

        userModel.setIdRole(ROLE_EMPLOYEE_ID);
        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        userPersistencePort.saveUser(userModel);
    }

    private void validateEmail(String email){
        if (email==null ||!emainPattern.matcher(email).matches()){
            throw new InvalidEmailException();
        }
    }

    private void validateAge(LocalDate birthDate){
        if (birthDate==null || Period.between(birthDate,LocalDate.now(ZONE_ID)).getYears() < MIN_AGE){
            throw new InvalidUserAgeException();
        }
    }

    private void validateCellphone(String cellphone){
        if (cellphone==null|| cellphone.length()>13 || !cellphonePattern.matcher(cellphone).matches()){
            throw new InvalidCellphoneException();
        }
    }

    private void validateDocument(String document){
        if (document.isEmpty() || !documentPattern.matcher(document).matches()){
            throw new InvalidDocumentException();
        }
    }
}
