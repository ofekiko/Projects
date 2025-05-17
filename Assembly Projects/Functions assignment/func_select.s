/* 322374240 Ofek Kaharizi */
.extern printf
.extern scanf

.section .rodata
    invalid_option: .string "invalid option!\n"
    first_Pstring_length: .string "first pstring length: %d, "
    second_Pstring_length: .string "second pstring length: %d\n"
    scanf_2num: .string "%d %d"

.section .text
.globl run_func
.type	run_func, @function 
run_func:
    push %rbp
    movq %rsp, %rbp  

    # jump according to the input of the choice
    movq %rdi, %rax
    
    cmpq $31 ,%rax
    je pstrlen31
    
    cmpq $33 ,%rax
    je swapCase33
    
    cmpq $34 ,%rax
    je pstrijcpy34
    
    cmpq $37 ,%rax
    je pstrcat37
    
    jmp invalid_choice

pstrlen31:
    subq $16 ,%rsp # Allocating memory space for both pstrings 
    movq %rsi, (%rsp) # Pushing the first Pstring into the stack
    movq %rdx, 8(%rsp) # Pushing the second Pstring into the stack

    # Calling the pstrlen function for the first Pstring
    movq (%rsp), %rdi 
    call pstrlen

    # Print the result
    movq $first_Pstring_length, %rdi
    movq %rax, %rsi
    xorq %rax, %rax
    call printf

    # Calling the pstrlen function for the second Pstring
    movq 8(%rsp), %rdi
    call pstrlen

    # Print the result
    movq $second_Pstring_length, %rdi
    movq %rax, %rsi
    xorq %rax, %rax
    call printf

    # Reallocating the stack
    addq $16, %rsp
    jmp done
    
swapCase33:
    subq $16 ,%rsp # Allocating memory space for both pstrings 
    movq %rsi, (%rsp) # Pushing the first Pstring into the stack
    movq %rdx, 8(%rsp) # Pushing the second Pstring into the stack

    # Calls the function for the first pstring
    movq (%rsp), %rdi
    call swapCase
    
    # Calls the function for the second pstring
    movq 8(%rsp), %rdi
    call swapCase

    # Reallocating the stack
    addq $16, %rsp
    jmp done

pstrijcpy34:
    subq $32 ,%rsp # Allocating memory space for both pstrings 
    movq %rsi, (%rsp) # Pushing the first Pstring into the stack
    movq %rdx, 8(%rsp) # Pushing the second Pstring into the stack

    # Reserve space on the stack to store the two integers read by scanf
    movq $scanf_2num, %rdi  # First argument is the format string
    leaq 16(%rsp), %rsi      # Address of where to store the second number
    leaq 24(%rsp), %rdx      # Address of where to store the first number
    xorq %rax, %rax
    call scanf

    movq (%rsp), %rdi
    movq 8(%rsp), %rsi
    movq 16(%rsp), %rdx
    movq 24(%rsp), %rcx
    call pstrijcpy

    # Reallocating the stack
    addq $32, %rsp
    jmp done

pstrcat37:
    subq $16 ,%rsp # Allocating memory space for both pstrings 
    movq %rsi, (%rsp) # Pushing the first Pstring into the stack
    movq %rdx, 8(%rsp) # Pushing the second Pstring into the stack

    movq (%rsp), %rdi
    movq 8(%rsp), %rsi
    call pstrcat

    # Reallocating the stack
    addq $16, %rsp
    jmp done

invalid_choice:
    # prints invalid option and closes the program
    movq $invalid_option,%rdi
    xorq %rax, %rax
    call printf

    jmp done
done:
    # closes the program
    xorq %rax, %rax
    popq %rbp
    ret
