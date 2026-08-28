package com.shyam.service.Imp;

import static com.shyam.constants.MessageConstant.*;

import com.shyam.common.util.MessageSourceUtil;
import com.shyam.dao.HomeServiceDAO;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import com.shyam.entity.ServiceHome;
import com.shyam.mapper.HomeServiceMapper;
import com.shyam.service.HomeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class HomeServiceImp implements HomeService {
  private final HomeServiceDAO homeServiceDAO;
  private final MessageSourceUtil messageSourceUtil;

  @Override
  @Transactional(readOnly = true)
  public GetAllHomeServiceResponseDTO getAllHomeServiceRequests() {
    List<ServiceHome> services = homeServiceDAO.getAllHomeServiceRequests();
    return HomeServiceMapper.getAllHomeServiceRequests(services);
  }

  @Override
  @Transactional(readOnly = true)
  public GetAllHomeServiceResponseDTO searchHomeServiceRequest(
      SearchHomeServiceRequestDTO searchHomeServiceRequestDTO) {
    List<ServiceHome> services =
        homeServiceDAO.searchHomeServices(searchHomeServiceRequestDTO.getKeyword());
    return HomeServiceMapper.getAllHomeServiceRequests(services);
  }

  @Override
  @Transactional(readOnly = true)
  public HomeServiceResponseDTO getHomeServiceRequestById(
      HomeServiceRequestDTO homeServiceRequestDTO) {
    var service = homeServiceDAO.findHomeService(homeServiceRequestDTO.getServiceId());
    return HomeServiceMapper.getHomeServiceRequestById(service);
  }

  @Override
  @Transactional(readOnly = true)
  public GetAllHomeServiceResponseDTO getAllUserServiceRequests() {
    return null;
  }

  @Override
  @Transactional
  public CreateHomeServiceResponseDTO createHomeServiceRequests(
      CreateHomeServiceRequestDTO createHomeServiceRequestDTO) {
    var service = HomeServiceMapper.createHomeServiceRequests(createHomeServiceRequestDTO);
    homeServiceDAO.saveHomeService(service);
    return HomeServiceMapper.maoToCreateHomeServiceResponseDTO(
        messageSourceUtil.getMessage(MESSAGE_CODE_CREATE_HOME_SERVICE));
  }

  @Override
  @Transactional
  public EditHomeServiceResponseDTO editHomeServiceRequest(EditHomeServiceRequestDTO dto) {
    var service = homeServiceDAO.findHomeService(dto.getServiceId());
    var serviceMapper = HomeServiceMapper.editHomeServiceRequest(service, dto);
    homeServiceDAO.saveHomeService(serviceMapper);
    return HomeServiceMapper.mapTOEditHomeServiceResponseDTO(
        messageSourceUtil.getMessage(MESSAGE_CODE_EDIT_HOME_SERVICE));
  }

  @Override
  @Transactional
  public DeleteHomeServiceResponseDTO deleteHomeServiceRequest(
      DeleteHomeServiceRequestDTO deleteHomeServiceRequestDTO) {
    var service = homeServiceDAO.findHomeService(deleteHomeServiceRequestDTO.getServiceId());
    homeServiceDAO.deleteHomeService(service.getServiceId());
    return HomeServiceMapper.mapTODeleteHomeServiceResponseDTO(
        messageSourceUtil.getMessage(MESSAGE_CODE_DELETE_HOME_SERVICE));
  }
}
