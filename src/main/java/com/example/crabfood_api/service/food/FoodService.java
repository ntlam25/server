package com.example.crabfood_api.service.food;

import com.example.crabfood_api.dto.request.FoodOptionRequest;
import com.example.crabfood_api.dto.request.FoodRequest;
import com.example.crabfood_api.dto.request.OptionChoiceRequest;
import com.example.crabfood_api.dto.response.FoodResponse;
import com.example.crabfood_api.exception.ResourceNotFoundException;
import com.example.crabfood_api.model.entity.*;
import com.example.crabfood_api.repository.CategoryRepository;
import com.example.crabfood_api.repository.FoodOptionRepository;
import com.example.crabfood_api.repository.FoodRepository;
import com.example.crabfood_api.repository.VendorRepository;
import com.example.crabfood_api.service.AbstractCrudService;
import com.example.crabfood_api.util.Mapper;
import com.example.crabfood_api.util.UserUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FoodService extends AbstractCrudService<FoodRequest, FoodResponse, FoodRepository, Food, Long>
    implements IFoodService {

    private final UserUtil userUtil;

    private final VendorRepository vendorRepository;
    private final CategoryRepository categoryRepository;
    private final FoodOptionRepository foodOptionRepository;

    protected FoodService(FoodRepository repository, UserUtil userUtil, VendorRepository vendorRepository,
                          CategoryRepository categoryRepository,
                          FoodOptionRepository foodOptionRepository) {
        super(repository, Food.class);
        this.userUtil = userUtil;
        this.vendorRepository = vendorRepository;
        this.categoryRepository = categoryRepository;
        this.foodOptionRepository = foodOptionRepository;
    }

    @Transactional
    @Override
    protected Food createAndSave(FoodRequest request) {
        User user = userUtil.getCurrentUser();
        Vendor vendor = vendorRepository.findByUserId(
                user.getId()).orElseThrow(() ->
                new ResourceNotFoundException("Vendor with id " + user.getId() + " not found"));
        Food food = Mapper.toFoodEntity(request,vendor);

        List<Category> categories = getCategories(request);

        food.setCategories(new HashSet<>(categories));
        return repository.save(food);
    }

    private List<Category> getCategories(FoodRequest request) {
        List<Long> categoryIds = request.getCategoryIds();
        List<Category> categories = categoryRepository.findAllById(categoryIds);

        if (categories.size() != categoryIds.size()) {
            // Tìm ra ID nào bị thiếu
            Set<Long> foundIds = categories.stream().map(Category::getId).collect(Collectors.toSet());
            List<Long> missingIds = categoryIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            throw new RuntimeException("Category ID(s) not found: " + missingIds);
        }
        return categories;
    }

    @Transactional
    @Override
    protected Food updateAndSave(Long id, FoodRequest request) {
        Food food = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food with id " + id + " not found"));

        // Cập nhật thông tin cơ bản
        food.setName(request.getName());
        food.setDescription(request.getDescription());
        food.setPrice(request.getPrice());
        food.setImageUrl(request.getImageUrl());
        food.setAvailable(request.isAvailable());
        food.setFeatured(request.isFeatured());
        food.setPreparationTime(request.getPreparationTime());

        // Cập nhật danh mục
        List<Category> categories = getCategories(request);
        food.getCategories().clear();               // Xoá các liên kết cũ
        food.getCategories().addAll(categories);

        // Cập nhật FoodOption và OptionChoice
        List<FoodOption> existingOptions = food.getOptions() != null ? food.getOptions() : new ArrayList<>();
        List<Long> incomingOptionIds = new ArrayList<>();
        List<FoodOption> updatedOptions = new ArrayList<>();

        if (request.getOptions() != null) {
            for (FoodOptionRequest optionReq : request.getOptions()) {
                FoodOption option;

                if (optionReq.getId() != null) {
                    option = existingOptions.stream()
                            .filter(o -> o.getId().equals(optionReq.getId()))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Option with id " + optionReq.getId() + " not found"));

                    option.setName(optionReq.getName());
                    option.setRequired(optionReq.isRequired());
                    option.setMinSelection(optionReq.getMinSelection());
                    option.setMaxSelection(optionReq.getMaxSelection());
                    incomingOptionIds.add(option.getId());
                } else {
                    option = new FoodOption();
                    option.setFood(food);
                    option.setName(optionReq.getName());
                    option.setRequired(optionReq.isRequired());
                    option.setMinSelection(optionReq.getMinSelection());
                    option.setMaxSelection(optionReq.getMaxSelection());
                }

                // Cập nhật OptionChoice
                List<OptionChoice> existingChoices = option.getChoices() != null ? option.getChoices() : new ArrayList<>();
                List<Long> incomingChoiceIds = new ArrayList<>();
                List<OptionChoice> updatedChoices = new ArrayList<>();

                if (optionReq.getChoices() != null) {
                    for (OptionChoiceRequest choiceReq : optionReq.getChoices()) {
                        OptionChoice choice;

                        if (choiceReq.getId() != null) {
                            choice = existingChoices.stream()
                                    .filter(c -> c.getId().equals(choiceReq.getId()))
                                    .findFirst()
                                    .orElseThrow(() -> new ResourceNotFoundException("Choice with id " + choiceReq.getId() + " not found"));

                            choice.setName(choiceReq.getName());
                            choice.setPriceAdjustment(choiceReq.getPriceAdjustment());
                            choice.setDefault(choiceReq.isDefault());
                            incomingChoiceIds.add(choice.getId());
                        } else {
                            choice = new OptionChoice();
                            choice.setOption(option);
                            choice.setName(choiceReq.getName());
                            choice.setPriceAdjustment(choiceReq.getPriceAdjustment());
                            choice.setDefault(choiceReq.isDefault());
                        }

                        updatedChoices.add(choice);
                    }
                }

                // Xóa choice không còn
                existingChoices.removeIf(c -> c.getId() != null && !incomingChoiceIds.contains(c.getId()));
                existingChoices.addAll(updatedChoices);
                option.setChoices(existingChoices);

                updatedOptions.add(option);
            }
        }

        // Xóa option không còn
        existingOptions.removeIf(o -> o.getId() != null && !incomingOptionIds.contains(o.getId()));
        existingOptions.addAll(updatedOptions);
        food.setOptions(existingOptions);

        return repository.save(food);
    }


    @Override
    protected FoodResponse toResponse(Food domainEntity) {
        return Mapper.toFoodResponse(domainEntity);
    }

    @Override
    public List<FoodResponse> findByCategoryId(Long categoryId) {
        return repository.findByCategoryId(categoryId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<FoodResponse> findByVendorId(Long vendorId) {
        return repository.findByVendorId(vendorId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<FoodResponse> findTopFoodsByVendorId(Long vendorId, int limit) {
        return repository.findTopFeaturedByVendor(vendorId, limit).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<FoodResponse> findPopularFoodsByVendorId(Long vendorId, int limit) {
        return repository.findPopularFoodByVendorId(vendorId, limit).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<FoodResponse> findNewFoodsByVendorId(Long vendorId, int limit) {
        return repository.findNewFoodsByVendorId(vendorId, limit).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<FoodResponse> findByVendorIdAndCategoryId(Long vendorId, Long categoryId) {
        return repository.findByVendorIdAndCategoryId(vendorId, categoryId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public FoodResponse updateStatusAvailable(Long id, boolean available) {
        Food food = repository.findById(id).orElseThrow(
                () ->  new ResourceNotFoundException("Food with id " + id + " not found"));
        food.setAvailable(available);
        return Mapper.toFoodResponse(repository.save(food));
    }

    @Override
    public List<FoodResponse> findAll() {
        return repository.findAllByIsDeletedIsFalse().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        Food food = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food with id " + id + " not found"));
        food.setDeleted(true);
        repository.save(food);
    }
}
