package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.util.enums.BookerStatus;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.RequestCreateCommentDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.RequestItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.util.enums.ItemStatus;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    private final ItemServiceImpl itemService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository requestRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    private User user;
    private Item item1;
    private Item item2;

    @BeforeEach
    public void setUp() {
        user = new User(null, "testUser", "test@example.com");

        item1 = new Item(null, "Item 1", "Description 1", ItemStatus.AVAILABLE, user, null);
        item2 = new Item(null, "Item 2", "Description 2", ItemStatus.AVAILABLE, user, null);
    }

    @Test
    public void findAllItemsByUserId_shouldReturnItems() {
        Long userId = userRepository.save(user).getId();
        itemRepository.save(item1);
        itemRepository.save(item2);

        Collection<ItemDto> items = itemService.findAllItemsByUserId(userId);

        assertThat(items).isNotNull();
        assertThat(items).isNotEmpty();
        assertThat(items.size()).isEqualTo(2);
    }

    @Test
    public void findAllItemsByUserId_shouldReturnEmptyCollectionOfItems() {
        Collection<ItemDto> items = itemService.findAllItemsByUserId(999L);

        assertThat(items).isNotNull();
        assertThat(items).isEmpty();
    }

    @Test
    public void searchByNameAndDescription_shouldReturnItems() {
        userRepository.save(user);

        String substring = "&3276_substring_8291&";
        item1.setDescription(substring);
        item2.setDescription(substring);
        itemRepository.save(item1);
        itemRepository.save(item2);


        Collection<ItemDto> items = itemService.searchByNameAndDescription(substring);

        assertThat(items).isNotNull();
        assertThat(items).isNotEmpty();
        assertThat(items.size()).isEqualTo(2);
    }

    @Test
    public void searchByNameAndDescription_shouldReturnEmptyCollectionOfItems() {
        Collection<ItemDto> items = itemService.searchByNameAndDescription("WKDO-192K-FKWK-JS2J");

        assertThat(items).isNotNull();
        assertThat(items).isEmpty();

        items = itemService.searchByNameAndDescription("");

        assertThat(items).isNotNull();
        assertThat(items).isEmpty();

        items = itemService.searchByNameAndDescription(null);

        assertThat(items).isNotNull();
        assertThat(items).isEmpty();
    }

    @Test
    public void saveCommentToItem_commentShouldBeSaved() {
        userRepository.save(user);
        Item storageItem = itemRepository.save(item1);
        Long itemId = storageItem.getId();

        User storageBooker = userRepository.save(new User(null, "Booker", "booker@gmail.com"));
        Long userId = storageBooker.getId();
        bookingRepository.save(
                new Booking(null,
                        LocalDateTime.now().minusDays(2),
                        LocalDateTime.now().minusDays(1),
                        storageItem,
                        storageBooker,
                        BookerStatus.APPROVED));

        RequestCreateCommentDto requestCreateCommentDto = new RequestCreateCommentDto("Comment 1");

        CommentDto savedComment = itemService.saveCommentToItem(userId, itemId, requestCreateCommentDto);

        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getText()).isEqualTo(requestCreateCommentDto.getText());
        assertThat(savedComment.getAuthorName()).isEqualTo(storageBooker.getName());
    }

    @Test
    public void saveCommentToItem_shouldThrowNotFoundException() {
        userRepository.save(user);
        Item storageItem = itemRepository.save(item1);
        Long itemId = storageItem.getId();

        RequestCreateCommentDto requestCreateCommentDto = new RequestCreateCommentDto("Comment 1");

        assertThatThrownBy(() -> itemService.saveCommentToItem(999L, itemId, requestCreateCommentDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void saveCommentToItem_shouldThrowAccessDeniedException() {
        userRepository.save(user);
        itemRepository.save(item1);

        User storageBooker = userRepository.save(new User(null, "Booker", "booker@gmail.com"));
        Long userId = storageBooker.getId();

        RequestCreateCommentDto requestCreateCommentDto = new RequestCreateCommentDto("Comment 1");

        assertThatThrownBy(() -> itemService.saveCommentToItem(userId, item1.getId(), requestCreateCommentDto))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining(
                        format("User{id=%d} не может оставлять комментарии к Item{id=%d}", userId, item1.getId()));
    }

    @Test
    public void create_itemShouldBeCreatedWithNullRequest() {
        userRepository.save(user);
        RequestItemDto requestItemDto = new RequestItemDto("name", "description", true);

        ItemDto created = itemService.create(user.getId(), requestItemDto);
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo(requestItemDto.getName());
        assertThat(created.getDescription()).isEqualTo(requestItemDto.getDescription());
        assertThat(created.getStatus()).isEqualTo(requestItemDto.getAvailable());
    }

    @Test
    public void create_itemShouldBeCreatedWithNotNulRequest() {
        User requestor = new User(null, "userName-1", "user_description1@example.com");
        ItemRequest request = new ItemRequest(null, "requestDesc-1", requestor, LocalDateTime.now());

        userRepository.save(user);
        userRepository.save(requestor);
        requestRepository.save(request);

        RequestItemDto requestItemDto =
                new RequestItemDto(item1.getName(), item1.getDescription(), item1.isAvailable(), request.getId());

        ItemDto created = itemService.create(user.getId(), requestItemDto);
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo(requestItemDto.getName());
        assertThat(created.getDescription()).isEqualTo(requestItemDto.getDescription());
        assertThat(created.getStatus()).isEqualTo(requestItemDto.getAvailable());
        assertThat(created.getRequest()).isEqualTo(requestItemDto.getRequestId());
    }

    @Test
    public void create_shouldThrowNotFoundException_whereWrongRequestId() {
        long requestId = 999L;
        userRepository.save(user);
        RequestItemDto requestItemDto = new RequestItemDto("name", "description", true, requestId);

        assertThatThrownBy(() -> itemService.create(user.getId(), requestItemDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(format("ItemRequest по id=%d не найден", requestId));
    }

    @Test
    public void create_shouldThrowNotFoundException_whereWrongUserId() {
        User requestor = new User(null, "userName-1", "user_description1@example.com");
        ItemRequest request = new ItemRequest(null, "requestDesc-1", requestor, LocalDateTime.now());
        userRepository.save(requestor);
        requestRepository.save(request);

        RequestItemDto requestItemDto = new RequestItemDto("name", "description", true, request.getId());

        Long wrongUserId = 999L;
        assertThatThrownBy(() -> itemService.create(wrongUserId, requestItemDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(format("User по id=%s не найден", wrongUserId));
    }

    @Test
    public void findItemById_shouldReturnItemWithNullRequest() {
        userRepository.save(user);
        itemRepository.save(item1);

        ItemDto foundItem = itemService.findItemById(item1.getId());
        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getId()).isEqualTo(item1.getId());
        assertThat(foundItem.getRequest()).isEqualTo(null);
    }

    @Test
    public void findItemById_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> itemService.findItemById(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void update_ShouldBeUpdated_whenAllParamsAreNotNull() {
        Long userId = userRepository.save(user).getId();
        Long itemId = itemRepository.save(item1).getId();

        RequestItemDto requestItemDto =
                new RequestItemDto("NSJS-1919-DJOW-JDLD", "LDJF-7394-KEOC-QVPD", !item1.isAvailable());

        ItemDto itemDto = itemService.update(userId, itemId, requestItemDto);
        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getId()).isEqualTo(itemId);
        assertThat(itemDto.getName()).isEqualTo(requestItemDto.getName());
        assertThat(itemDto.getDescription()).isEqualTo(requestItemDto.getDescription());
        assertThat(itemDto.getStatus()).isEqualTo(requestItemDto.getAvailable());
    }

    @Test
    public void update_ShouldBeUpdated_whenAllParamsAreNull() {
        Long userId = userRepository.save(user).getId();
        Long itemId = itemRepository.save(item1).getId();

        RequestItemDto requestItemDto =
                new RequestItemDto(null, null, null);

        ItemDto itemDto = itemService.update(userId, itemId, requestItemDto);
        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getId()).isEqualTo(itemId);
        assertThat(itemDto.getName()).isEqualTo(item1.getName());
        assertThat(itemDto.getDescription()).isEqualTo(item1.getDescription());
        assertThat(itemDto.getStatus()).isEqualTo(item1.isAvailable());
    }

    @Test
    public void update_ShouldBeUpdated_whenAllParamsAreNullOrBlanc() {
        Long userId = userRepository.save(user).getId();
        Long itemId = itemRepository.save(item1).getId();

        RequestItemDto requestItemDto =
                new RequestItemDto("  ", "    ", null);

        ItemDto itemDto = itemService.update(userId, itemId, requestItemDto);
        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getId()).isEqualTo(itemId);
        assertThat(itemDto.getName()).isEqualTo(item1.getName());
        assertThat(itemDto.getDescription()).isEqualTo(item1.getDescription());
        assertThat(itemDto.getStatus()).isEqualTo(item1.isAvailable());
    }

    @Test
    public void update_shouldThrow_whenWrongUserId() {
        userRepository.save(user);
        Long itemId = itemRepository.save(item1).getId();

        RequestItemDto requestItemDto = new RequestItemDto(null, null, null);

        assertThatThrownBy(() -> itemService.update(999L, itemId, requestItemDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void update_shouldThrow_whenWrongUserIsNotOwner() {
        User notOwner = new User(null, "userName-1", "user_description1@example.com");
        userRepository.save(notOwner);
        userRepository.save(user);
        Long itemId = itemRepository.save(item1).getId();

        RequestItemDto requestItemDto = new RequestItemDto(null, null, null);

        assertThatThrownBy(() -> itemService.update(notOwner.getId(), itemId, requestItemDto))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage(format("User{id=%d} не имеет доступ к Item{id=%d}", notOwner.getId(), itemId));
    }
}

