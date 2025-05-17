/* 322374240 Ofek Kaharizi */
.extern printf
.extern scanf
.section .rodata
invalid_input: .string "invalid input!\n"
length_fmt: .string "length: %d, "
string_fmt: .string "string: %s\n"
cant_concate: .string "cannot concatenate strings!\n"
.section .text

.globl pstrlen
pstrlen:
    pushq %rbp
    movq %rsp, %rbp

    xorq %rax, %rax
    movzbl (%rdi), %eax

    popq %rbp
    ret

.globl swapCase
swapCase:
    pushq %rbp
    movq %rsp, %rbp

    movq $0, %rcx # Counter for the loop

    movzbq (%rdi), %r12 # Saving the length of the string in a register
    leaq 1(%rdi), %r13 # Allocating the string itself in rsi without the length

    .loop33:
        cmp  %r12, %rcx # Checks if the counter is equal to the length, if it is, end
        je .done33

        # moves the letter into a register
        leaq (%r13, %rcx, 1), %rax  # Effective address = %r13 + (%ecx * 1)
        movb (%rax), %bl

        # Checks if its a lower case letter, if not, go to next letter
        cmpb $122, %bl
        ja prep_loop

        cmpb $97, %bl
        jl check_if_upper

        # If it is a lower case letter, make it large
        subb $32, %bl
        movb %bl, (%rax) # returns the new letter into its place

        # loops again
        jmp prep_loop
    
    check_if_upper:
        # Checks if below 65 (not a letter at all)
        cmpb $65 ,%bl
        jl prep_loop

        # If its between 96-91 its not a letter
        cmpb $90 ,%bl
        ja prep_loop

        # The value is between 90 and 65 so its a capital letter, change to small
        addb $32, %bl
        movb %bl, (%rax) # returns the new letter into its place

        jmp prep_loop

    prep_loop:
        # Increase the counter
        incq %rcx
        jmp .loop33

    .done33:
        # Print the result
        movq $length_fmt, %rdi
        movq %r12, %rsi
        xorq %rax, %rax
        call printf

        # Print the result
        movq $string_fmt, %rdi
        movq %r13, %rsi
        xorq %rax, %rax
        call printf

        popq %rbp
        ret

.globl pstrijcpy
pstrijcpy:
    pushq %rbp
    movq %rsp, %rbp

    movzbl (%rdi), %r8d # First string length
    movzbl (%rsi), %r9d # Second string length
    leaq 1(%rdi), %r12 # First Pstring
    leaq 1(%rsi) ,%r13 # Second Pstring
    movzbq %dl, %r14   # i (start index) zero-extended to 64 bits
    movzbq %cl, %r15   # j (end index) zero-extended to 64 bits

    # Initializing a counter
    xorq %rcx, %rcx
    addq %r14, %rcx

    .check_input:
        # Is i larger than j
        cmpq %r14, %r15
        jl .invalid_input

        # Is i smaller than 0
        cmpq $0, %r14
        jl invalid_input

        # Is j smaller than 0
        cmpq $0, %r15
        jl invalid_input

        # Is i larger than the first string
        cmpq %r14, %r8
        jle .invalid_input

        # Is j larger than the first string
        cmpq %r15, %r8
        jle .invalid_input

        # Is i larger than the second string
        cmpq %r14, %r9
        jle .invalid_input

        # Is j larger than the second string
        cmpq %r15, %r9
        jle .invalid_input

        # If we reach here, input is likely valid (non-negative indices within bounds)
        jmp .loop34

    .loop34:
        leaq (%r13, %rcx, 1), %rax # Load the byte of the second string
        movb (%rax), %bl
        movb %bl ,(%r12, %rcx, 1) # Change the corresponding byte in the first string

        cmpq %rcx, %r15
        je .done34

        # Increase counter and start again
        incq %rcx
        jmp .loop34
    
    .invalid_input:
         # moves the lengths so it wont be overwritten by the printf
        movq %r8, %r14
        movq %r9, %r15

        movq $invalid_input,%rdi
        xorq %rax, %rax
        call printf
        jmp continue_done

    .done34:
        # moves the lengths so it wont be overwritten by the printf
        movq %r8, %r14
        movq %r9, %r15
    
    continue_done:
        # Print the length of the new first string 
        movq $length_fmt, %rdi
        movq %r14, %rsi
        xorq %rax, %rax
        call printf

        # Print the new first string
        movq $string_fmt, %rdi
        movq %r12, %rsi
        xorq %rax, %rax
        call printf

         # Print the length of the second string
        movq $length_fmt, %rdi
        movq %r15, %rsi
        xorq %rax, %rax
        call printf

        # Print the second string
        movq $string_fmt, %rdi
        movq %r13, %rsi
        xorq %rax, %rax
        call printf

        popq %rbp
        ret
    
.globl pstrcat
pstrcat:
    pushq %rbp
    movq %rsp, %rbp

    movzbq (%rdi), %r12 # Length of First Pstring
    leaq 1(%rdi), %r13 # First Pstring

    movzbq (%rsi), %r14 # Length of Second Pstring
    leaq 1(%rsi), %r15 # Second Pstring

    # Check if length greater than 254, if it is, end run
    xorq %rax, %rax
    addq %r12, %rax
    addq %r14, %rax
    cmpq $254, %rax
    ja .too_big

    xorq %rcx, %rcx # Counter for the second string
    movq %r13, %rdx # Start at the beginning of the first string
    addq %r12, %rdx # Move the pointer to the end of the first string 
    movq %rax, %r12 # Save length of the combined length

    .loop37:
        cmpq %r14, %rcx # Check if the the loop went over all the letters of the second string
        je .done

        leaq (%r15, %rcx, 1), %rax # Load the byte of the second string
        movb (%rax), %bl
        movb %bl ,(%rdx, %rcx, 1) # Add the byte to the first string

        # Increase counter and start again
        incq %rcx
        jmp .loop37

    .too_big:
        # Print the cant_concate message
        movq $cant_concate, %rdi
        xorq %rax, %rax
        call printf

        jmp .done

    .done:  
         # Print the length of the new first string 
        movq $length_fmt, %rdi
        movq %r12, %rsi
        xorq %rax, %rax
        call printf

        # Print the new first string
        movq $string_fmt, %rdi
        movq %r13, %rsi
        xorq %rax, %rax
        call printf

         # Print the length of the second string
        movq $length_fmt, %rdi
        movq %r14, %rsi
        xorq %rax, %rax
        call printf

        # Print the second string
        movq $string_fmt, %rdi
        movq %r15, %rsi
        xorq %rax, %rax
        call printf

        popq %rbp
        ret
