package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.util.enums.ItemStatus;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {
    private final ItemRequestServiceImpl requestService;
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    User requestor;
    ItemRequest itemRequest;
    User responseOwner;
    Item response;

    @BeforeEach
    public void setUp() {
        requestor = new User(null, "Requestor-1", "Requestor-1::/description");
        responseOwner = new User(null, "ResponseOwner-1", "ResponseOwner-1::/description");
        itemRequest = new ItemRequest(null,
                "ItemRequest-1::/description", requestor, LocalDateTime.now().minusDays(1));
        response = new Item(null,
                "Item-1", "Item-1::/description", ItemStatus.AVAILABLE, responseOwner, itemRequest);
    }

    @Test
    public void findAllRequestsOfUser_shouldReturnRequests() {
        requestor = userRepository.save(requestor);
        responseOwner = userRepository.save(responseOwner);
        itemRequest = requestRepository.save(itemRequest);
        response = itemRepository.save(response);

        Collection<ItemRequestDto> allRequestsOfUser = requestService.findAllRequestsOfUser(requestor.getId());
        assertThat(allRequestsOfUser).isNotNull();
        assertThat(allRequestsOfUser).isNotEmpty();
        assertThat(allRequestsOfUser.size()).isEqualTo(1);
        assertThat(allRequestsOfUser.iterator().next().getResponses()
                .stream().anyMatch(i -> i.getId().equals(response.getId()))).isTrue();
    }

    @Test
    public void findAll_shouldReturnRequests() {
        requestor = userRepository.save(requestor);
        responseOwner = userRepository.save(responseOwner);
        itemRequest = requestRepository.save(itemRequest);
        response = itemRepository.save(response);

        Collection<ItemRequestDto> allRequests = requestService.findAll(responseOwner.getId());
        assertThat(allRequests).isNotNull();
        assertThat(allRequests).isNotEmpty();
        assertThat(allRequests.size()).isEqualTo(1);
    }

    @Test
    public void create_requestShouldBeCreated() {
        requestor = userRepository.save(requestor);
        responseOwner = userRepository.save(responseOwner);

        RequestItemRequestDto requestItemRequestDto = new RequestItemRequestDto("Description-1");
        ItemRequestDto created = requestService.create(requestor.getId(), requestItemRequestDto);

        Optional<ItemRequest> createdFromStorageOpt = requestRepository.findById(created.getId());
        assertThat(createdFromStorageOpt.isPresent()).isTrue();
        assertThat(createdFromStorageOpt.get().getDescription()).isEqualTo(requestItemRequestDto.getDescription());
    }

    @Test
    public void findItemRequestBy_shouldReturnRequest() {
        requestor = userRepository.save(requestor);
        responseOwner = userRepository.save(responseOwner);
        ItemRequest request = new ItemRequest(null, "Description-1", requestor, LocalDateTime.now());
        requestRepository.save(request);

        ItemRequestDto foundRequest = requestService.findItemRequestBy(request.getId());

        assertThat(foundRequest).isNotNull();
        assertThat(foundRequest.getId()).isEqualTo(request.getId());
        assertThat(foundRequest.getCreated()).isEqualTo(request.getCreated());
        assertThat(foundRequest.getResponses()).isEmpty();
    }

    @Test
    public void findItemRequestBy_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> requestService.findItemRequestBy(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void create_shouldThrowNotFoundException() {
        RequestItemRequestDto requestItemRequestDto = new RequestItemRequestDto("Description-1");
        assertThatThrownBy(() -> requestService.create(999L, requestItemRequestDto))
                .isInstanceOf(NotFoundException.class);
    }
}
