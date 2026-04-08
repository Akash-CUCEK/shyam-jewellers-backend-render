package com.shyam.dao;

import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.constants.ErrorCodeConstants;
import com.shyam.entity.Users;
import com.shyam.repository.UsersRepo;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDAO {
  private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);
  private final UsersRepo usersRepo;

  public Users findUser(String email) {
    logger.debug("Finding user with email: {}", email);

    return usersRepo
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
    return usersRepo.findByEmail(email);
  }

  public Users save(Users user) {
    try {
      logger.debug("Saving the user: {}", user.getEmail());
      usersRepo.save(user);
      return user;
    } catch (Exception e) {
      logger.error("Error while saving user: {}", user.getEmail(), e);
      throw new SYMException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          SYMErrorType.GENERIC_EXCEPTION,
          ErrorCodeConstants.ERROR_CODE_AUTHZ_UNKNOWN,
          String.format("Failed to save user with email %s", user.getEmail()),
          e.getMessage());
    }
  }
}
