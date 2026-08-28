package com.shyam.payment.gateway;

import com.shyam.common.constants.PaymentGateway;
import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.constants.ErrorCodeConstants;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class PaymentGatewayProviderFactory {

  private final Map<PaymentGateway, PaymentGatewayProvider> providers =
      new EnumMap<>(PaymentGateway.class);

  public PaymentGatewayProviderFactory(List<PaymentGatewayProvider> providerList) {
    providerList.forEach(provider -> providers.put(provider.gateway(), provider));
  }

  public PaymentGatewayProvider getProvider(PaymentGateway gateway) {
    PaymentGatewayProvider provider = providers.get(gateway);
    if (provider != null) {
      return provider;
    }

    throw new SYMException(
        HttpStatus.BAD_REQUEST,
        SYMErrorType.USER_ERROR,
        ErrorCodeConstants.ERROR_CODE_VALIDATION,
        "Payment gateway is not supported",
        "No provider configured for gateway " + gateway);
  }
}
