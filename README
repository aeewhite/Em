# The Em Programming Language

Em is an interpreted, dynamically typed language written in Java. Em programs typically have a file extension of `.em`.

## Syntax

### Variables

Variables are defined with the `var` keyword and are dynamically typed.

```
var a = 5;
var b = 1;
var c = a + b;
var a = "Hello, World!";
```

### Functions

Functions are defined by the `function` keyword, an identifier, a list of formal parameters, and a body. Em features implicit returns rather than explicit, so no `return`s are necessary. The last statement to be evaluated in a function will be it's return value.

```
function fib(x){
	if(x < 2){
		x;
	}
	else{
		fib((x - 1)) + fib((x - 2));
	}
}
```

As seen in the example functions can be recursive.

Functions in Em are first-class, and can be passed around just like any other variable (including as return values from functions). 

#### Lambdas

Em supports lambdas as anonymous functions. Here is an example of their syntax:

```
lambda(x, y) -> {x * y;};

// Lambdas can be assigned to variables and act just like functions

var multiply = lambda(x, y) -> {x * y;};

multiply(4, 5); //20
```

### Conditionals

Em has support for the boolean operators `&&`, `||` as well as `<`, `>`, `<=`, `>=`, `==`, `!=`, and `is` for checking object equality. Boolean expressions can be negated with `!(expr)`.

Em supports `if` statements followed by optional `else if` and `else` statements

```
if(a > 5){
	
}
else if(a == 5){

}
else{
	//a will be less than 5
}
```

### Looping

Em supports 4 kinds of looping: `while`, `for`, `do..while`, and recursion. Examples of each follow:

```
var a = 5;
while(a > 0){
	print(a, "\n");
	--a;
}

for(var a = 5; a > 0; ++a){
	print("In the loop\n");
}

var a = 5;
do{
	print(--a, "\n");
}while(a > 0);

//Recursion shown above
```

## Comments

Line comments can delineated either with `//` or `#`.

The `#` comment can be used in a [shebang](https://en.wikipedia.org/wiki/Shebang_(Unix)) to run Em scripts directly. For example `#!/usr/bin/env emi` if you have `emi` (the Em interpreter) installed on your system.

## Features

### Unary Operators

#### `!` Negation

Booleans can be negated by placing a `!` in front of them.

```
!(true); //false
```

#### `++` Increment and `--` Decrement

Variables with integer values can be incremented or decremented using the `++` and `--` operators respectively.

```
var a = 5;
--a;
//a == 4
++a;
//a == 5
```

### Object Oriented

Em has two ways of defining Objects. First is to create a function and return its environment at the end of the definition

```
//Traditional Way of Defining an object
function book(book_title){
	var title = book_title;

	function setTitle(newTitle){
		title = newTitle;
	}

	this;
}
```

Em also provides a `class` keyword as syntactic sugar. Changing the above example to use `class` would look like this:

```
class book(book_title){
	var title = book_title;

	function setTitle(newTitle){
		title = newTitle;
	}
	function getTitle(){title;}
}
```

Using the `class` keyword removes the need to return `this` at the end of the definition. When using the `class` keyword, the object itself can be referenced using the `self` variable. For example, a node in a tree might reference `self.getParent().getValue()`.

Example of how to use objects in Em:

```
var CS_Textbook = book("Structure and Interpretation of Computer Programs");
print(CS_Textbook.title, "\n");
CS_Textbook.setTitle("Introduction to Computer Algorithms");
print(CS_Textbook.title, "\n");
```

Run the `objects` and `objectsx` make targets to see this in action.

### Operator Precedence

Em will evaluate mathematical in correct order of operations. For example, `18 - 6 * 2` will evaluate to `6`. Parenthesis can be used to force a desired evaluation order if necessary. For example, `(18 - 6) * 2` will evaluate to `24`.

Run the `precedence` and `precedencex` make targets to see this in action.

### Emoji 😎

Em supports emoji everywhere in the language. Variable names as well as their value can have emoji names.

```
var emoji = "😎";

var 💩 = "'High Quality Code'";
```

As a consequence of supporting emoji, other special characters are also supported.

```
var encyclopædia = "fancy encyclopedia";

var 🌵 = "Señor Cactus";

println(encyclopædia);
println(🌵);
```

Run the `emoji` and `emojix` make targets to see this in action.


## Usage

Run `make` to produce a `dpl` script that will start the interpreter.

To run an Em program, execute `dpl filename` from a shell


