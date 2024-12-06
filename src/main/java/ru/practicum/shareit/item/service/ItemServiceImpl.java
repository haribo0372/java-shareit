package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.base.service.BaseInDbService;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.RequestCreateCommentDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.RequestCreateItemDto;
import ru.practicum.shareit.item.dto.item.RequestUpdateItemDto;
import ru.practicum.shareit.item.dto.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
public class ItemServiceImpl extends BaseInDbService<Item, ItemRepository> implements ItemService {
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    protected ItemServiceImpl(ItemRepository repository,
                              UserService userService, CommentRepository commentRepository, BookingRepository bookingRepository) {
        super(repository, Item.class);
        this.userService = userService;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public ItemDto update(Long userId, Long itemId, RequestUpdateItemDto itemDto) {
        Item item = ItemMapper.fromDto(itemId, itemDto);
        User user = userService.findById(userId);
        Item storageItem = super.findById(item.getId());
        if (!storageItem.getOwner().equals(user))
            throw new AccessDeniedException(
                    format("User{id=%d} не имеет доступ к Item{id=%d}", userId, item.getId()), 403);

        if (item.getName() != null && !item.getName().isBlank())
            storageItem.setName(item.getName());
        if (item.getDescription() != null && !item.getDescription().isBlank())
            storageItem.setDescription(item.getDescription());
        if (item.getStatus() != null) {
            storageItem.setStatus(item.getStatus());
        }

        log.info("Item{id={}} у User{id={}} успешно обновлен", storageItem.getId(), userId);
        return this.toDto(storageItem);
    }

    @Override
    public ItemDto findItemById(Long itemId) {
        return this.toDto(this.findById(itemId), commentRepository.findAllByItemId(itemId));
    }

    @Override
    public Collection<ItemDto> findAllItemsByUserId(Long userId) {
        List<ItemDto> foundItems = super.repository.findAllByOwnerId(userId);
        foundItems.forEach(itemDto -> itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId())));
        log.debug("Все Item, принадлежающие пользователю User{id{}} успешно найдены", userId);
        return foundItems;
    }

    @Override
    public Collection<ItemDto> searchByNameAndDescription(Long ownerId, String text) {
        Collection<ItemDto> foundItems = super.repository.findFilteredItemsByOwnerId(ownerId, text);

        log.debug("Все Item's, принадлежащие пользователю User{id={}} и имеющие подстроку {} " +
                "в поле name или description успешно найдены (size={})", ownerId, text, foundItems.size());
        return foundItems;
    }

    @Override
    public CommentDto saveCommentToItem(Long userId, Long itemId, RequestCreateCommentDto requestCreateCommentDto) {
        LocalDateTime dateTime = LocalDateTime.now();

        User storageUser = userService.findById(userId);
        Item storageItem = super.findById(itemId);

        if (!bookingRepository.existsByBookerIdAndItemIdAndEndDateTimeIsBefore(userId, itemId, dateTime))
            throw new AccessDeniedException(
                    format("User{id=%d} не может оставлять комментарии к Item{id=%d}", userId, itemId), 400);

        Comment comment = CommentMapper.fromDto(requestCreateCommentDto);
        comment.setItem(storageItem);
        comment.setAuthor(storageUser);
        comment.setCreated(dateTime);
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment{id={}} от User{id={}} к Item{id={}} успешно добавлен", savedComment.getId(), userId, itemId);
        return CommentMapper.toDto(savedComment);
    }

    @Override
    public ItemDto create(Long userId, RequestCreateItemDto requestItemDto) {
        Item item = ItemMapper.fromDto(requestItemDto);
        User foundUser = userService.findById(userId);
        item.setOwner(foundUser);
        Item savedItem = super.save(item);
        log.info("Item{id={}} успешно сохранен у пользователя User{id={}}", savedItem.getId(), userId);
        return this.toDto(savedItem);
    }


    private ItemDto toDto(Item item) {
        return ItemMapper.toItemDto(item);
    }

    private ItemDto toDto(Item item, Collection<CommentDto> comments) {
        return ItemMapper.toItemDto(item, comments);
    }
}
