package com.shyam.dao;

import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.entity.Users;
import com.shyam.repository.UsersRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDAO {
  private final UsersRepository usersRepository;

  public Users findUser(String email) {
    log.debug("Finding user with email: {}", email);

    return usersRepository
        .findByEmail(email)
        .orElseThrow(
            () ->
                new SYMException(
                    HttpStatus.NOT_FOUND,
                    SYMErrorType.GENERIC_EXCEPTION,
                    ErrorCodeConstants.ERROR_CODE_AUTHZ_USER_NOT_EXIST,
                    String.format("User with email %s not found", email),
                    String.format("Login attempted with email %s which does not exist", email)));
  }

  public Optional<Users> findOnlyUser(String email) {
    return usersRepository.findByEmail(email);
  }

  public Users save(Users user) {
    try {
      log.debug("Saving the user: {}", user.getEmail());
      usersRepository.save(user);
      return user;
    } catch (Exception e) {
      log.error("Error while saving user: {}", user.getEmail(), e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          String.format("Failed to save user with email %s", user.getEmail()),
          e.getMessage());
    }
  }
}
