package com.example.library.controller;

import com.example.library.exception.ResourceNotFoundException;
import com.example.library.model.Loan;
import com.example.library.model.Member;
import com.example.library.repository.LoanRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Loan> getLoanById(@PathVariable Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id " + id));
        return ResponseEntity.ok(loan);
    }

    @PostMapping
    public ResponseEntity<Object> createLoan(@RequestBody Loan loan) {
        Member member = memberRepository.findById(loan.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id " + loan.getMemberId()));

        if (loanRepository.findByMemberId(member.getId()).size() >= 5) {
            return ResponseEntity.badRequest().body("Member already has 5 loans");
        }

        bookRepository.findById(loan.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + loan.getBookId()));

        // Set the lend date to today if not provided
        if (loan.getLendDate() == null) {
            loan.setLendDate(LocalDate.now());
        }

        // Set the return date to one week from the lend date if not provided
        if (loan.getReturnDate() == null) {
            loan.setReturnDate(loan.getLendDate().plusWeeks(1));
        }

        Loan savedLoan = loanRepository.save(loan);
        member.getLoanIds().add(savedLoan.getId());
        memberRepository.save(member);
        return ResponseEntity.ok(savedLoan);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Loan> updateLoan(@PathVariable Long id, @RequestBody Loan loanDetails) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id " + id));
        memberRepository.findById(loanDetails.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id " + loanDetails.getMemberId()));
        bookRepository.findById(loanDetails.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + loanDetails.getBookId()));

        loan.setMemberId(loanDetails.getMemberId());
        loan.setBookId(loanDetails.getBookId());
        loan.setLendDate(loanDetails.getLendDate());
        loan.setReturnDate(loanDetails.getReturnDate());
        Loan updatedLoan = loanRepository.save(loan);
        return ResponseEntity.ok(updatedLoan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id " + id));

        Member member = memberRepository.findById(loan.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id " + loan.getMemberId()));
        member.getLoanIds().remove(loan.getId());
        memberRepository.save(member);

        loanRepository.delete(loan);
        return ResponseEntity.noContent().build();
    }
}
