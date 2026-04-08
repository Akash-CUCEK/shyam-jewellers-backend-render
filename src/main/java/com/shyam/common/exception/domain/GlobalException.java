package com.shyam.common.exception.domain;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.common.exception.dto.ErrorMessagesDTO;
import com.shyam.common.exception.dto.ErrorResponseDTO;
import java.time.LocalDateTime;
import java.util.Collections;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {

  @ExceptionHandler(SYMException.class)
  public ResponseEntity<BaseResponseDTO<Void>> handleSRYException(SYMException sym) {

    var errorMessagesDTO = new ErrorMessagesDTO(sym.getMessage());

    var errorResponseDTO =
        new ErrorResponseDTO(
            Collections.singletonList(errorMessagesDTO), LocalDateTime.now(), sym.getErrorType());

    BaseResponseDTO<Void> baseResponse = new BaseResponseDTO<>(null, errorResponseDTO);

    return new ResponseEntity<>(baseResponse, sym.getStatus());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<BaseResponseDTO<Void>> handleGenericException(Exception ex) {

    ErrorMessagesDTO errorMessagesDTO =
        new ErrorMessagesDTO("Something went wrong. Please try again later.");

    ErrorResponseDTO errorResponseDTO =
        new ErrorResponseDTO(
            Collections.singletonList(errorMessagesDTO),
            LocalDateTime.now(),
            SYMErrorType.GENERIC_EXCEPTION);

    BaseResponseDTO<Void> baseResponse = new BaseResponseDTO<>(null, errorResponseDTO);

    return new ResponseEntity<>(baseResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
