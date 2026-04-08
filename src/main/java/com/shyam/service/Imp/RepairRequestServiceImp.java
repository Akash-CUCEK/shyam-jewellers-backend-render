package com.shyam.service.Imp;

import static com.shyam.constants.MessageConstant.*;

import com.shyam.common.util.MessageSourceUtil;
import com.shyam.dao.RepairRequestDAO;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import com.shyam.entity.RepairService;
import com.shyam.mapper.RepairRequestMapper;
import com.shyam.service.RepairRequestService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepairRequestServiceImp implements RepairRequestService {

  private final RepairRequestDAO repairRequestDAO;
  private final MessageSourceUtil messageSourceUtil;

  @Override
  public GetAllRepairResponseDTO getAllRepairRequests() {
    List<RepairService> repairRequest = repairRequestDAO.findAllRepairRequest();
    return RepairRequestMapper.getAllRepairRequests(repairRequest);
  }

  @Override
  public RepairRequestResponseDTO getRepairRequestById(
      RepairRequestRequestDTO repairRequestRequestDTO) {
    var service = repairRequestDAO.findRepairRequest(repairRequestRequestDTO.getServiceId());
    return RepairRequestMapper.getRepairRequestById(service);
  }

  @Override
  public GetAllRepairResponseDTO searchRepairRequest(
      SearchRepairRequestDTO searchRepairRequestDTO) {
    List<RepairService> services =
        repairRequestDAO.searchRepairRequests(searchRepairRequestDTO.getKeyword());
    return RepairRequestMapper.getAllRepairRequests(services);
  }

  @Override
  public CreateRepairResponseDTO createRepairRequest(
      CreateRepairRequestDTO createRepairRequestDTO) {
    var service = RepairRequestMapper.createRepairRequest(createRepairRequestDTO);
    repairRequestDAO.saveRepairRequest(service);
    return RepairRequestMapper.maoToCreatRepairRequestResponseDTO(
        messageSourceUtil.getMessage(MESSAGE_CODE_CREATE_REPAIR_SERVICE));
  }

  @Override
  public EditRepairResponseDTO editRepairRequest(EditRepairRequestDTO editRepairRequestDTO) {
    var service = repairRequestDAO.findRepairRequest(editRepairRequestDTO.getServiceId());
    var serviceMapper = RepairRequestMapper.editRepairRequest(service, editRepairRequestDTO);
    repairRequestDAO.saveRepairRequest(serviceMapper);
    return RepairRequestMapper.mapTOEditRepairRequestResponseDTO(
        messageSourceUtil.getMessage(MESSAGE_CODE_EDIT_REPAIR_SERVICE));
  }

  @Override
  public DeleteRepairResponseDTO deleteRepairRequest(
      DeleteRepairRequestDTO deleteRepairRequestDTO) {
    var service = repairRequestDAO.findRepairRequest(deleteRepairRequestDTO.getServiceId());
    repairRequestDAO.deleteRepairService(deleteRepairRequestDTO.getServiceId());
    return RepairRequestMapper.mapTODeleteRepairRequestResponseDTO(
        messageSourceUtil.getMessage(MESSAGE_CODE_DELETE_REPAIR_SERVICE));
  }
}
