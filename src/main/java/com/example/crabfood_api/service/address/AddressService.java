package com.example.crabfood_api.service.address;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.crabfood_api.dto.request.AddressRequest;
import com.example.crabfood_api.dto.request.GpsCoordinates;
import com.example.crabfood_api.dto.response.AddressResponse;
import com.example.crabfood_api.dto.response.HereGeocodingResult;
import com.example.crabfood_api.exception.ResourceNotFoundException;
import com.example.crabfood_api.model.entity.Address;
import com.example.crabfood_api.model.entity.User;
import com.example.crabfood_api.repository.AddressRepository;
import com.example.crabfood_api.service.AbstractCrudService;
import com.example.crabfood_api.service.maps.HereMapsSyncService;
import com.example.crabfood_api.util.Mapper;
import com.example.crabfood_api.util.UserUtil;

@Service
public class AddressService
        extends AbstractCrudService<AddressRequest, AddressResponse, AddressRepository, Address, Long>
        implements IAddressService {

    private final UserUtil userUtil;
    private final HereMapsSyncService hereMapsService;

    protected AddressService(AddressRepository repository, UserUtil userUtil,
            HereMapsSyncService hereMapsService) {
        super(repository, Address.class);
        this.userUtil = userUtil;
        this.hereMapsService = hereMapsService;
    }

    public List<AddressResponse> getUserAddresses() {
        User user = userUtil.getCurrentUser();
        return repository.findByUser(user).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public HereGeocodingResult getAddressFromGps(GpsCoordinates coordinates) {
        return hereMapsService.reverseGeocode(coordinates);
    }

    @Override
    protected Address createAndSave(AddressRequest request) {
        User user = userUtil.getCurrentUser();

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            repository.findByUserAndIsDefault(user, true)
                    .forEach(address -> address.setIsDefault(false));
        }

        Address address = Address.builder()
                .fullAddress(request.getFullAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .label(request.getLabel())
                .isDefault(request.getIsDefault())
                .user(user)
                .build();

        return repository.save(address);
    }

    @Override
    protected Address updateAndSave(Long id, AddressRequest request) {
        User user = userUtil.getCurrentUser();
        Address address = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Address with id " + id + " not found"));

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            repository.findByUserAndIsDefault(user, true)
                    .forEach(addr -> addr.setIsDefault(false));
        }

        address.setLabel(request.getLabel());
        address.setFullAddress(request.getFullAddress());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        address.setIsDefault(request.getIsDefault());

        return repository.save(address);
    }

    @Override
    public void deleteById(Long id) {
        User user = userUtil.getCurrentUser();
        Address address = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Address with id " + id + " not found"));

        repository.delete(address);

        // Nếu xóa địa chỉ mặc định, chọn 1 địa chỉ khác làm mặc định
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            repository.findByUser(user).stream()
                    .findFirst()
                    .ifPresent(addr -> {
                        addr.setIsDefault(true);
                        repository.save(addr);
                    });
        }
    }

    @Override
    protected AddressResponse toResponse(Address domainEntity) {
        return Mapper.toAddressResponse(domainEntity);
    }
}