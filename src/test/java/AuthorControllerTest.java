import com.example.library.controller.AuthorController;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.model.Author;
import com.example.library.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthorControllerTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorController authorController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllAuthors_Success() {
        List<Author> authors = new ArrayList<>();
        authors.add(new Author());

        when(authorRepository.findAll()).thenReturn(authors);

        ResponseEntity<List<Author>> response = authorController.getAllAuthors();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(authorRepository, times(1)).findAll();
    }

    @Test
    void getAuthorById_Success() {
        Author author = new Author();
        author.setId(1L);

        when(authorRepository.findById(anyLong())).thenReturn(Optional.of(author));

        ResponseEntity<Author> response = authorController.getAuthorById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(author, response.getBody());
    }

    @Test
    void getAuthorById_NotFound() {
        when(authorRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            authorController.getAuthorById(1L);
        });

        assertEquals("Author not found with id 1", exception.getMessage());
    }

    @Test
    void createAuthor_Success() {
        Author author = new Author();
        author.setId(1L);

        when(authorRepository.save(any(Author.class))).thenReturn(author);

        ResponseEntity<Author> response = authorController.createAuthor(author);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(authorRepository, times(1)).save(author);
    }

    @Test
    void updateAuthor_Success() {
        Author author = new Author();
        author.setId(1L);

        when(authorRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        Author updatedDetails = new Author();
        updatedDetails.setName("Updated Name");
        updatedDetails.setDateOfBirth(LocalDate.of(1980, 1, 1));
        ResponseEntity<Author> response = authorController.updateAuthor(1L, updatedDetails);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated Name", response.getBody().getName());
        verify(authorRepository, times(1)).save(author);
    }

    @Test
    void deleteAuthor_Success() {
        Author author = new Author();
        author.setId(1L);

        when(authorRepository.findById(anyLong())).thenReturn(Optional.of(author));

        ResponseEntity<Void> response = authorController.deleteAuthor(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(authorRepository, times(1)).delete(author);
    }
}