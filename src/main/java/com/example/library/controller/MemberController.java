package com.example.library.controller;

import com.example.library.exception.ResourceNotFoundException;
import com.example.library.model.Member;
import com.example.library.repository.MemberRepository;
import com.example.library.repository.LoanRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LoanRepository loanRepository;


    @Operation(summary = "Get a list of all members")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the members",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Member.class))}),
            @ApiResponse(responseCode = "404", description = "Members not found",
                    content = @Content)
    })
    @GetMapping
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Operation(summary = "Get a member by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the member",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Member.class))}),
            @ApiResponse(responseCode = "404", description = "Member not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id " + id));
        return ResponseEntity.ok(member);
    }

    @Operation(summary = "Create a new member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Member.class))})
    })
    @PostMapping
    public Member createMember(@RequestBody Member member) {
        return memberRepository.save(member);
    }


    @Operation(summary = "Update an existing member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Member.class))}),
            @ApiResponse(responseCode = "404", description = "Member not found",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @RequestBody Member memberDetails) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id " + id));
        member.setUsername(memberDetails.getUsername());
        member.setEmail(memberDetails.getEmail());
        member.setAddress(memberDetails.getAddress());
        member.setPhoneNumber(memberDetails.getPhoneNumber());
        member.setLoanIds(memberDetails.getLoanIds());
        Member updatedMember = memberRepository.save(member);
        return ResponseEntity.ok(updatedMember);
    }


    @Operation(summary = "Delete a member by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Member deleted",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Member not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id " + id));
        memberRepository.delete(member);
        return ResponseEntity.noContent().build();
    }
}
