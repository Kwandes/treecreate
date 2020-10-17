package dev.hotdeals.treecreate.model;

import dev.hotdeals.treecreate.repository.UserRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserTests
{
    @Autowired
    UserRepo userRepo;

    @Test
    @DisplayName("The database gets populated")
    void dbPopulationTest()
    {
        assertThat(userRepo.existsById(1)).isTrue();
    }

    @Test
    @DisplayName("The database data is correct: ID")
    void dbPopulationIdTest()
    {
        assertThat(userRepo.findById(1).get().getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("The database data is correct: Name")
    void dbPopulationNameTest()
    {
        assertThat(userRepo.findById(1).get().getName()).isEqualTo("tester");
    }

    @Test
    @DisplayName("The database data is correct: email")
    void dbPopulationEmailTest()
    {
        assertThat(userRepo.findById(1).get().getEmail()).isEqualTo("test@treecreate.dk");
    }

    @Test
    @DisplayName("The database data is correct: Password")
    void dbPopulationPasswordTest()
    {
        assertThat(userRepo.findById(1).get().getPassword()).isEqualTo("$2a$10$Ad/owORhm9WIA.hDSZ76juwvUuNLicDINElvqGw35uBp10m/ta3Um");
    }

    @Test
    @DisplayName("The database data is correct: Phone Number")
    void dbPopulationPhoneNumberTest()
    {
        assertThat(userRepo.findById(1).get().getPhoneNumber()).isEqualTo("12345678");
    }

    @Test
    @DisplayName("The database data is correct: Street Address")
    void dbPopulationStreetAddressTest()
    {
        assertThat(userRepo.findById(1).get().getStreetAddress()).isEqualTo("Yeetgade 69");
    }

    @Test
    @DisplayName("The database data is correct: City")
    void dbPopulationCityTest()
    {
        assertThat(userRepo.findById(1).get().getCity()).isEqualTo("Copenhagen");
    }

    @Test
    @DisplayName("The database data is correct: Postcode")
    void dbPopulationPostcodeTest()
    {
        assertThat(userRepo.findById(1).get().getPostcode()).isEqualTo("69");
    }

    @Test
    @DisplayName("The database data is correct: Access Level")
    void dbPopulationAccessLevelTest()
    {
        assertThat(userRepo.findById(1).get().getAccessLevel()).isEqualTo(0);
    }

    @Test
    @DisplayName("The toString generates properly")
    void userToStringTest()
    {
        assertThat(userRepo.findById(1).get().toString().length() > 10).isTrue();
    }

    @Test
    @DisplayName("The full Constructor works")
    void userFullConstructorTest()
    {
        User user = new User(3, "name", "test@treecreate.dk", "test",
                "123", "Yeetgade 69", "Cph", "420", 0);
        assertThat(user.toString().length() > 10).isTrue();
    }

}