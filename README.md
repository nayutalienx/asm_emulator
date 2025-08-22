# ASM Emulator

**Software model of an educational microcomputer with an educational assembly language**

A comprehensive educational tool for learning assembly language programming, featuring a complete development environment with assembler, emulator, and visual debugging capabilities.

<img src="https://github.com/nayutalienx/asm_emulator/blob/master/pic.png" alt="ASM Emulator Screenshot" border="0">

## Overview

ASM Emulator is an educational microcomputer simulation designed to help students and enthusiasts learn assembly language programming concepts. The project provides a complete development environment including:

- **Assembly Code Editor** with syntax highlighting
- **Assembler** that translates assembly code to machine code
- **Machine Emulator** that executes the compiled programs step-by-step
- **Visual Debugger** showing processor state, memory contents, and registers
- **Real-time Execution** with status monitoring

## Features

- 📝 **Rich Text Editor** with assembly syntax highlighting and line numbers
- 🔧 **Two-Pass Assembler** with comprehensive error reporting and diagnostics
- 🖥️ **8-bit Microprocessor Emulator** with realistic instruction execution
- 🧮 **Arithmetic Logic Unit (ALU)** with flag support (Zero, Minus, Overflow, Carry)
- 💾 **256-byte RAM** with hexadecimal and binary visualization
- 📊 **Register Monitoring** showing processor state in real-time
- 🐛 **Step-by-step Debugging** capabilities
- 📋 **Listing Generation** showing assembled code with addresses
- 🔢 **Binary Output** display for understanding machine code

## Instruction Set Architecture

The emulated processor supports the following instruction types:

### Arithmetic Operations
- `ADD` - Add register to accumulator
- `AND` - Bitwise AND operation  
- `OR` - Bitwise OR operation

### Data Transfer
- `LDRC` - Load constant into register
- `LDRB` - Load byte from memory into register
- `SVRB` - Store register byte to memory

### Control Flow
- `JMP` - Unconditional jump
- `JMPI` - Indirect jump
- `JZ/JNZ` - Jump if zero/not zero
- `JM/JNM` - Jump if minus/not minus
- `JOV/JNOV` - Jump if overflow/not overflow
- `JC/JNC` - Jump if carry/not carry

### Assembler Directives
- `ORG` - Set origin address
- `DATA` - Define data bytes
- `END` - End of program

### Registers
- **4 General Purpose Registers**: R0, R1, R2, R3 (also accessible as A, B, C, D)
- **Program Counter (PC)**: Tracks current instruction address
- **Status Flags**: Zero (Z), Minus (M), Overflow (OV), Carry (C)

## Project Structure

```
asm_emulator/
├── asm_gui/                              # JavaFX GUI Application
│   ├── src/sample/
│   │   ├── Controller.java               # Main GUI controller
│   │   ├── EditorColoring.java           # Syntax highlighting
│   │   ├── EmulatorData.java             # Data model for emulator state
│   │   ├── FileProcessing.java           # File I/O operations
│   │   └── Main.java                     # Application entry point
│   └── libs/                             # External dependencies
├── micro_comp_console_syntax/            # Assembler Module
│   └── src/com/company/
│       ├── Asm.java                      # Main assembler class
│       ├── SourceLine.java               # Source code parsing
│       ├── DiagLine.java                 # Error diagnostics
│       └── ListingLine.java              # Assembly listing generation
├── micro_comp_console_machine_emulator/  # Machine Emulator Module
│   └── src/com/company/
│       ├── Proc.java                     # Processor simulation
│       ├── ALU.java                      # Arithmetic Logic Unit
│       └── RAM.java                      # Memory simulation
└── libs/                                 # Shared libraries
    ├── commons-lang3-3.9.jar
    └── richtextfx-fat-0.10.1.jar
```

## Getting Started

### Prerequisites

- **Java 8 or higher** with JavaFX support
- **IntelliJ IDEA** (recommended) or any Java IDE
- The following libraries are included in the `libs/` directory:
  - RichTextFX 0.10.1 (for the code editor)
  - Apache Commons Lang 3.9

### Building and Running

1. **Clone the repository:**
   ```bash
   git clone https://github.com/nayutalienx/asm_emulator.git
   cd asm_emulator
   ```

2. **Open in IntelliJ IDEA:**
   - Open the `asm_gui` project folder
   - Ensure the libraries in `libs/` are added to the classpath
   - The project should auto-configure with the included `.iml` files

3. **Run the application:**
   - Execute the `Main.java` class in `asm_gui/src/sample/`
   - The GUI application will launch

### Alternative Build Method

Each module can be compiled separately:

1. **Compile the assembler:**
   ```bash
   cd micro_comp_console_syntax
   javac -d out src/com/company/*.java
   ```

2. **Compile the emulator:**
   ```bash
   cd micro_comp_console_machine_emulator  
   javac -d out src/com/company/*.java
   ```

3. **Compile the GUI (with dependencies):**
   ```bash
   cd asm_gui
   javac -cp "libs/*" -d out src/sample/*.java
   ```

## Usage

### Writing Assembly Programs

1. **Start the application** and use the code editor on the left panel
2. **Write your assembly code** using the supported instruction set
3. **Click "Translate"** or enable auto-translation to compile your code
4. **View the listing** in the middle panel showing addresses and machine code
5. **See binary output** in the right panel

### Example Program

```assembly
ORG 1H                    ; Start at address 1H
LDRB a,30H               ; Load byte from address 30H into register A
LDRB b,32H               ; Load byte from address 32H into register B  
ADD a,b                  ; Add B to A, result in A
SVRB a,34H               ; Store result to address 34H
END                      ; End program

ORG 30H                  ; Data section at 30H
DATA 25H                 ; First operand = 37 decimal
DATA 0FAH                ; Second operand = 250 decimal  
DATA 0                   ; Result storage
```

### Running Programs

1. **Load the compiled program** into memory using "Load to Memory"
2. **Click "Run"** to execute the entire program at once
3. **Use "Step"** for step-by-step debugging
4. **Monitor the processor state** in the bottom panel
5. **View memory contents** and register values in real-time

### Debugging Features

- **Memory Viewer**: Shows RAM contents in hex, binary, and decimal
- **Register Display**: Current values of all registers and flags
- **Status Log**: Real-time execution status and processor operations
- **Step Execution**: Execute one instruction at a time
- **Breakpoint Support**: Pause execution at specific points

## Educational Use

This emulator is designed for:

- **Computer Science Education**: Teaching processor architecture and assembly language concepts
- **Digital Logic Courses**: Demonstrating CPU operation and instruction execution
- **Systems Programming**: Understanding low-level programming concepts
- **Self-Study**: Learning assembly language in a visual, interactive environment

## Technical Details

### Processor Architecture
- **8-bit data width** with 8-bit addresses (256 bytes of memory)
- **Harvard architecture** (separate instruction and data paths)
- **4 general-purpose registers** with accumulator-based operations
- **Condition code flags** for program flow control

### Memory Organization
- **256 bytes of RAM** (addresses 00H to FFH)
- **Byte-addressable** memory organization
- **Little-endian** byte ordering for multi-byte data

### Assembly Language Syntax
- **Case-insensitive** instruction mnemonics
- **Hexadecimal constants** with 'H' suffix (e.g., `0AH`, `FFH`)
- **Register names**: R0-R3 or A, B, C, D
- **Comments** start with semicolon (`;`)
- **Labels** supported for jump targets

## Contributing

This is an educational project. Contributions that enhance the learning experience are welcome:

- Bug fixes and improvements
- Additional instruction set features  
- Enhanced debugging capabilities
- Documentation improvements
- Example programs and tutorials

## License

This project is intended for educational use. Please check with the original authors for specific licensing terms.

---

*Original Russian description: "Программная модель учебной микроэвм с учебным языком ассемблера"*
