import com.example.library.controller.MemberController;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.model.Member;
import com.example.library.repository.MemberRepository;
import com.example.library.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class MemberControllerTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private MemberController memberController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllMembers_Success() {
        List<Member> members = new ArrayList<>();
        members.add(new Member());

        when(memberRepository.findAll()).thenReturn(members);

        List<Member> result = memberController.getAllMembers();

        assertEquals(1, result.size());
        verify(memberRepository, times(1)).findAll();
    }

    @Test
    void getMemberById_Success() {
        Member member = new Member();
        member.setId(1L);

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        ResponseEntity<Member> response = memberController.getMemberById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(member, response.getBody());
    }

    @Test
    void getMemberById_NotFound() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            memberController.getMemberById(1L);
        });

        assertEquals("Member not found with id 1", exception.getMessage());
    }

    @Test
    void createMember_Success() {
        Member member = new Member();
        member.setId(1L);
        member.setUsername("johndoe");
        member.setEmail("john@example.com");
        member.setAddress("123 Main St");
        member.setPhoneNumber("1234567890");

        when(memberRepository.save(any(Member.class))).thenReturn(member);

        ResponseEntity<Member> response = (ResponseEntity<Member>) memberController.createMember(member);
        Member result = response.getBody();

        assertEquals(1L, result.getId());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void updateMember_Success() {
        Member member = new Member();
        member.setId(1L);

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member updatedDetails = new Member();
        updatedDetails.setUsername("newUsername");
        ResponseEntity<Member> response = memberController.updateMember(1L, updatedDetails);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("newUsername", response.getBody().getUsername());
        verify(memberRepository, times(1)).save(member);
    }

    @Test
    void deleteMember_Success() {
        Member member = new Member();
        member.setId(1L);

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        ResponseEntity<Void> response = memberController.deleteMember(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(memberRepository, times(1)).delete(member);
    }
}
