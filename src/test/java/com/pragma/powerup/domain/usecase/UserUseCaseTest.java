package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IRestaurantValidationPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IRestaurantValidationPort restaurantValidationPort;

    @InjectMocks
    private UserUseCase userUseCase;
    private UserModel userValid;
    private UserModel validEmployee;
    private UserModel validClient;

    private static final Long OWNER_ID=1L;

    @BeforeEach
    void setUp(){
        userValid= new UserModel(null,"Santiago", "Pineda", "1022598694",
                "+57315687459", LocalDate.now().minusYears(21),
                "santiago@test.com","contrasena",0L,1L);
        validEmployee= new UserModel(null, "Juan", "Perez", "1122334455",
                "+573009998877", LocalDate.now().minusYears(20),
                "juan.perez@correo.com", "contrasena", null, 5L);
        validClient= new UserModel(null, "Esteban", "Castro", "232123321",
                "+573213215467", LocalDate.now().minusYears(34),
                "Esteban.Castro@correo.com", "contrasena", null,null);
    }

    @Test
    void saveOwner_whenUserValid_thenSaveOwner(){
        when(userPersistencePort.existsByEmail(userValid.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userValid.getPassword())).thenReturn("hashed");

        userUseCase.saveOwer(userValid);

        verify(userPersistencePort).saveUser(any(UserModel.class));
        assertEquals("hashed",userValid.getPassword());
    }

    @Test
    void saveOwner_whenEmailInvalid_thenThrowsException(){
        userValid.setEmail("emailnovalido");
        assertThrows(InvalidEmailException.class, () ->userUseCase.saveOwer(userValid));
        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    void saveOwner_whenCellphoneInvalid_thenThrowsException(){
        userValid.setCellphone("+312322233212233");
        assertThrows(InvalidCellphoneException.class, () ->userUseCase.saveOwer(userValid));
    }

    @Test
    void saveOwner_whenDocumentInvalid_thenThrowsException(){
        userValid.setDocument("qwerty12");
        assertThrows(InvalidDocumentException.class, () ->userUseCase.saveOwer(userValid));
    }

    @Test
    void saveOwner_whenAgeInvalid_thenThrowsException(){
        userValid.setBirthDate(LocalDate.now().minusYears(15));
        assertThrows(InvalidUserAgeException.class, () ->userUseCase.saveOwer(userValid));
    }

    @Test
    void saveOwner_whenEmailDuplicate_thenThrowsException(){
        when(userPersistencePort.existsByEmail(userValid.getEmail())).thenReturn(true);
        assertThrows(InvalidEmailDuplicate.class, () ->userUseCase.saveOwer(userValid));
        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    void saveEmployee_whenValidDataAndOwnerMatches_thenSaveWithEmployeeRole(){
        when(userPersistencePort.existsByEmail(validEmployee.getEmail()))
                .thenReturn(false);
        when(restaurantValidationPort.getRestaurantOwnerId(5L))
                .thenReturn(OWNER_ID);
        userUseCase.saveEmployee(validEmployee,OWNER_ID);
        verify(userPersistencePort).saveUser(any());
        assertEquals(3L,validEmployee.getIdRole());
    }

    @Test
    void saveEmployee_whenRestaurantBelongsToOtherOwner_thenThrowsException(){
        when(userPersistencePort.existsByEmail(validEmployee.getEmail()))
                .thenReturn(false);
        when(restaurantValidationPort.getRestaurantOwnerId(5L))
                .thenReturn(999L);
        assertThrows(UserNotOwnerRestaurantException.class, () ->userUseCase.saveEmployee(validEmployee,OWNER_ID));
        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    void saveEmployee_whenRestaurantNonExistent_thenThrowsException(){
        when(userPersistencePort.existsByEmail(validEmployee.getEmail()))
                .thenReturn(false);
        when(restaurantValidationPort.getRestaurantOwnerId(5L))
                .thenThrow(new UserNotOwnerRestaurantException());
        assertThrows(UserNotOwnerRestaurantException.class, () ->userUseCase.saveEmployee(validEmployee,OWNER_ID));
    }

    @Test
    void saveEmployee_whenEmailInvalid_thenThrowsExceptionBeforeCallingFeign(){
        validEmployee.setEmail("correo-sin-arroba");
        assertThrows(InvalidEmailException.class, () ->userUseCase.saveEmployee(validEmployee,OWNER_ID));
        verify(restaurantValidationPort, never()).getRestaurantOwnerId(any());
        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    void saveEmployee_whenEmailAlreadyRegistered_thenThrowsException(){
        when(userPersistencePort.existsByEmail(validEmployee.getEmail()))
                .thenReturn(true);
        assertThrows(InvalidEmailDuplicate.class, () ->userUseCase.saveEmployee(validEmployee,OWNER_ID));
        verify(restaurantValidationPort, never()).getRestaurantOwnerId(any());
    }

    @Test
    void saveClient_whenValidClient_thenSaveOwner(){
        when(userPersistencePort.existsByEmail(validClient.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(validClient.getPassword())).thenReturn("hashed");

        userUseCase.saveClient(validClient);

        verify(userPersistencePort).saveUser(any(UserModel.class));
        assertEquals("hashed",validClient.getPassword());
    }

    @Test
    void saveClient_whenEmailInvalid_thenThrowsException(){
        validClient.setEmail("emailnovalido");
        assertThrows(InvalidEmailException.class, () ->userUseCase.saveClient(validClient));
        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    void saveClient_whenCellphoneInvalid_thenThrowsException(){
        validClient.setCellphone("+312322233212233");
        assertThrows(InvalidCellphoneException.class, () ->userUseCase.saveClient(validClient));
    }

    @Test
    void saveClient_whenDocumentInvalid_thenThrowsException(){
        validClient.setDocument("qwerty12");
        assertThrows(InvalidDocumentException.class, () ->userUseCase.saveClient(validClient));
    }

    @Test
    void saveClient_whenAgeInvalid_thenThrowsException(){
        validClient.setBirthDate(LocalDate.now().minusYears(15));
        assertThrows(InvalidUserAgeException.class, () ->userUseCase.saveClient(validClient));
    }

    @Test
    void saveClient_whenEmailDuplicate_thenThrowsException(){
        when(userPersistencePort.existsByEmail(validClient.getEmail())).thenReturn(true);
        assertThrows(InvalidEmailDuplicate.class, () ->userUseCase.saveClient(validClient));
        verify(userPersistencePort, never()).saveUser(any());
    }
}
