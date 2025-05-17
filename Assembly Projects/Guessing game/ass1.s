/* 322374240 Ofek Kaharizi*/
.extern srand
.extern rand
.extern printf
.extern scanf

.section .data
user_seed: .long 0
user_guess: .long 0
num_wins: .long 0
num_tries: .long 0
max_tries: .long 5
choose_yn: .space 64, 0x0
range: .long 0b1010
right_num: .long 0
boolean_easy: .long 0

.section .rodata     # Read-only data section (for constants)
ask_seed: .string "Enter configuration seed: "
ask_easy: .string "Would you like to play in easy mode? (y/n) "
lower: .string "Your guess was below the actual number ...\n"
higher: .string "Your guess was above the actual number ...\n"
ask_guess: .string "What is your guess? "
double: .string "Double or nothing! Would you like to continue to another round? (y/n) "
lose_message: .string "\nGame over, you lost :(. The correct answer was %d\n"
result: .string "Congratz! you won %d rounds!\n"
incorret: .string "Incorrect. "
scanf_num: .string "%d"
scanf_yn: .string "%s"
.section .text
.globl main
.type main, @function

main:
    pushq %rbp        
    movq %rsp, %rbp

    jmp get_seed
get_seed:
    # print message to ask for seed
    movq $ask_seed, %rdi
    xorq %rax,%rax
    call printf

    # scan seed
    movq $scanf_num, %rdi
    leaq user_seed(%rip), %rsi
    xorq %rax, %rax
    call scanf

    # creating a new random number
    movl user_seed(%rip), %edi
    call srand
    call rand

    # making the number be in the correct by using module 
    movl range(%rip), %edi
    xorl %edx, %edx
    idiv %edi
    movl %edx, right_num(%rip)
    incl right_num(%rip)

    # ask for easy mode
    jmp easy_mode
easy_mode:
    # prints message
    movq $ask_easy,%rdi
    xorq %rax, %rax
    call printf

    # scans y or n
    movq $scanf_yn, %rdi
    leaq choose_yn(%rip) ,%rsi
    xorq %rax ,%rax
    call scanf

    # checks if its y or n and changes the boolean if y. continues normaly if n
    movq choose_yn(%rip), %rax
    cmpq $0x79, %rax
    je change_boolean

    # starts the loop if n
    cmpq $0x6e, %rax
    je guessing

change_boolean:
    # changes the boolean number for the easy message
    movl $1, boolean_easy(%rip)
    jmp guessing

guessing:
    # print message
    movq $ask_guess, %rdi
    xorq %rax,%rax
    call printf

    # scan guess
    movq $scanf_num, %rdi
    leaq user_guess(%rip), %rsi
    xorq %rax, %rax
    call scanf

    # compare user's guess to the correct number and if correct jump to win function
    movl user_guess(%rip), %eax
    movl right_num(%rip), %ebx
    cmpl %eax, %ebx
    je win

    # print incorrect message if guess is not right
    movq $incorret, %rdi
    xorq %rax, %rax
    call printf  

    # checks if the boolean is 1 inorder to see if we are in easy mode and act accordingly
    cmpl $1, boolean_easy(%rip)
    je easy_mode_message

    # decrease the number of tries and check if 0, if 0 finish the program
    incl num_tries(%rip)
    movl num_tries(%rip),%eax
    movl max_tries(%rip),%ebx
    cmpl %ebx, %eax
    je finish_lose
    
    # start of function
    jmp guessing

easy_mode_message:
    #  checks if guess below or above the right number and jumps accordingly
    movl right_num(%rip), %eax
    movl user_guess(%rip), %ebx
    cmpl %eax, %ebx
    jb print_below
    ja print_above

print_below:
    # prints message
    movq $lower, %rdi
    xorq %rax, %rax
    call printf 

    # decrease the number of tries and check if 0, if 0 finish the program
    incl num_tries(%rip)
    movl num_tries(%rip),%eax
    movl max_tries(%rip),%ebx
    cmpl %ebx, %eax
    je finish_lose

    # starts the loop again
    jmp guessing
print_above:
    # prints message
    movq $higher, %rdi
    xorq %rax, %rax
    call printf  

    # decrease the number of tries and check if 0, if 0 finish the program
    incl num_tries(%rip)
    movl num_tries(%rip),%eax
    movl max_tries(%rip),%ebx
    cmpl %ebx, %eax
    je finish_lose

    # starts the loop again
    jmp guessing
win:
    # increase number of wins
    incq num_wins(%rip)

    # prints message
    movq $double,%rdi
    xorq %rax, %rax
    call printf

    # scans y or n
    movq $scanf_yn, %rdi
    leaq choose_yn(%rip) ,%rsi
    xorq %rax ,%rax
    call scanf

    # checks if its y or n
    movq choose_yn(%rip), %rax
    cmpq $0x79, %rax
    je reset_game
    cmpq $0x6e, %rax
    je finish_win

    jmp done        

# series of actions to go to next round
reset_game:

    # reset number of tries
    movl $0, num_tries(%rip)
    # multiply the range of numbers and the seed by 2
    shll $1, range(%rip)
    shll $1, user_seed(%rip)

    # creating a new random number
    movl user_seed(%rip), %edi
    call srand
    call rand

    # making the number be in the correct by using module 
    movl range(%rip), %edi
    xorl %edx, %edx
    idiv %edi
    movl %edx, right_num(%rip)
    incl right_num(%rip)

    # starting the loop again
    jmp guessing

finish_win:
    # prints the ending result if chose to stop
    movq $result, %rdi
    movq num_wins(%rip), %rsi
    xorq %rax, %rax
    call printf

    jmp done
finish_lose:
    # prints after guessing to much and losing the game
    movq $lose_message, %rdi
    movq right_num(%rip), %rsi
    xorq %rax, %rax
    call printf

    jmp done
done:
    # closes the program
    xorq %rax, %rax
    popq %rbp
    ret
