# tkom-project



## Overview

Interpreter and language have been created as a project for `Compilation Technique` course.

## Language

The programming language is a custom combination of multiple cool features from many different, popular languages. 
It encompasses static typing and strong typing.
It had to allow optional values and by default all of variables are constant. To make variable mutable you have to use `mutable` keyword before the declaration.

[Defined grammar using ebnf notation](https://github.com/tedtheripper/tkom-interpreter/blob/main/docs/grammar.txt)

Features:
* support for 4 basic types: `bool`, `int`, `double` and `string`.
* single line comments: `# this is a comment`
* `string` type supports concatenation using `+` operator and escape characters
* defined operator precedence for both mathematical and logical expressions
* 2 conditional statements (`if [else]` and `while`)
* user can define custom functions
* supports recursive function call with stack overflow
* pass-by-value
* custom Rust-based pattern matching
* `??` operator which works like it does in C#
* `as` operator for casting between types
* `is` operator for type/null check

### Samples of the language

```rust
# function calculates Nth fibonacci number 
func fib(int n) : int { 
    if (n <= 1) {
        return n;
    }
    return fib(n - 2) + fib(n - 1);
}

mutable int i = 1;
int value = 13;
mutable double? sum = null;

while (i <= 10) {
    sum = (sum ?? 0.0) + (fib(i) * value as double);
    i = i + 1;
}

string resMessage = "Sum is: " + (sum as string);
print(resMessage);
```

```rust
func even(int value): bool {
    return value % 2 == 0;
}

func odd_and_divisible(int value, int div): bool {
    return value % 2 == 1 and value % div == 0;
}

string userInput = get_input();
 
match(userInput as int?) {     # return value of this expression can be accessed via '_'
    is int and even(_ as int) => print("Number" + (_ as string) + " is even"),
    is int and odd_and_divisible(_ as int, 3) => print("Number" +  (_ as string) + " is odd and divisible by 3"),
    default => print("Is not a number"),
}
```