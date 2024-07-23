package ControllerTest;

import com.example.library.controller.LoanController;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.model.Book;
import com.example.library.model.Loan;
import com.example.library.model.Member;
import com.example.library.repository.LoanRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.repository.BookRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class LoanControllerTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private LoanController loanController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createLoan_Success() {
        Loan loan = new Loan();
        loan.setMemberId(1L);
        loan.setBookId(1L);

        Member member = new Member();
        member.setId(1L);
        member.setLoanIds(new ArrayList<>());

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(new Book()));
        when(loanRepository.save(any(Loan.class))).thenAnswer(i -> {
            Loan l = i.getArgument(0);
            l.setId(1L);
            return l;
        });

        ResponseEntity<Object> response = loanController.createLoan(loan);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Loan);
        Loan savedLoan = (Loan) response.getBody();
        assertEquals(LocalDate.now().plusWeeks(1), savedLoan.getReturnDate());
        verify(memberRepository, times(1)).save(member);
    }

    @Test
    void createLoan_ExceedsLimit() {
        Loan loan = new Loan();
        loan.setMemberId(1L);
        loan.setBookId(1L);

        Member member = new Member();
        member.setId(1L);
        List<Loan> loans = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Loan existingLoan = new Loan();
            existingLoan.setId((long) i);
            loans.add(existingLoan);
        }
        member.setLoanIds(new ArrayList<>());

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(new Book()));
        when(loanRepository.findByMemberId(anyLong())).thenReturn(loans);

        ResponseEntity<Object> response = loanController.createLoan(loan);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Member already has 5 loans", response.getBody());
    }

    @Test
    void createLoan_MemberNotFound() {
        Loan loan = new Loan();
        loan.setMemberId(1L);
        loan.setBookId(1L);

        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            loanController.createLoan(loan);
        });

        assertEquals("Member not found with id 1", exception.getMessage());
    }

    @Test
    void createLoan_BookNotFound() {
        Loan loan = new Loan();
        loan.setMemberId(1L);
        loan.setBookId(1L);

        Member member = new Member();
        member.setId(1L);
        member.setLoanIds(new ArrayList<>());

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            loanController.createLoan(loan);
        });

        assertEquals("Book not found with id 1", exception.getMessage());
    }

    @Test
    void createLoan_WithNonExistingMember() {
        Loan loan = new Loan();
        loan.setMemberId(1L);
        loan.setBookId(1L);

        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            loanController.createLoan(loan);
        });

        assertEquals("Member not found with id 1", exception.getMessage());
        verify(loanRepository, never()).save(any(Loan.class));
    }
}