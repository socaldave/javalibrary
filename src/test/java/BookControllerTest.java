package ControllerTest;

import com.example.library.controller.BookController;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.model.Author;
import com.example.library.model.Book;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class BookControllerTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllBooks_Success() {
        List<Book> books = new ArrayList<>();
        books.add(new Book());

        when(bookRepository.findAll()).thenReturn(books);

        ResponseEntity<List<Book>> response = bookController.getAllBooks();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getBookById_Success() {
        Book book = new Book();
        book.setId(1L);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        ResponseEntity<Book> response = bookController.getBookById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(book, response.getBody());
    }

    @Test
    void getBookById_NotFound() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bookController.getBookById(1L);
        });

        assertEquals("Book not found with id 1", exception.getMessage());
    }

    @Test
    void createBook_Success() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("New Book");
        book.setGenre("Fiction");
        book.setPrice(BigDecimal.valueOf(19.99));
        book.setAuthorId(1L);

        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(authorRepository.findById(anyLong())).thenReturn(Optional.of(new Author()));

        ResponseEntity<Book> response = bookController.createBook(book);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void updateBook_Success() {
        Book book = new Book();
        book.setId(1L);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(authorRepository.findById(anyLong())).thenReturn(Optional.of(new Author()));

        Book updatedDetails = new Book();
        updatedDetails.setTitle("Updated Title");
        updatedDetails.setGenre("Updated Genre");
        updatedDetails.setPrice(BigDecimal.valueOf(29.99));
        updatedDetails.setAuthorId(1L);
        ResponseEntity<Book> response = bookController.updateBook(1L, updatedDetails);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated Title", response.getBody().getTitle());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void deleteBook_Success() {
        Book book = new Book();
        book.setId(1L);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        ResponseEntity<Void> response = bookController.deleteBook(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void createBook_WithNonExistingAuthor() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("New Book");
        book.setGenre("Fiction");
        book.setPrice(BigDecimal.valueOf(19.99));
        book.setAuthorId(1L);

        when(authorRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bookController.createBook(book);
        });

        assertEquals("Author not found with id 1", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class));
    }
}
