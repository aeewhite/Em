1;

"Hello, World!";

1+1;

1 - 1;

2 * 2;

4 / 2;

4 % 3;

"#ROLLTIDE!🐘";

var a = 25;

a + 5;

if(a < 1){
	1;
}
else if (a == 2){
	2;
}
else{
	3;
}

!true;

!false;

[1, 2, 3, 4, 5];

var arr = [1, 2, 3, 4, 5];

(1 + 2);

arr[3];

a = 4;

a + 1;

a = a + 2;

a;

++a;

a;

--a;

while(a > 0){
	--a;
}

if( 4 > 2  || 4 > 5 ){
	1;
}

if(4 == 4){
	4;
}

if ( 4 != 5){
	5;
}

for(var i = 0; i < 5; ++i){
	i;
}

var b = 3;

do{
	++b;
} while(b < 5);

arr[3] = 11;

function inc(a){
	a + 1;
}

var b = 5;

inc(b);

inc(1);

var lam = lambda(x)->{x - 1;};

lam;

lam(4);

function plusN(n){
	lambda(y)->{y+n;};
}

var plusFive = plusN(5);

plusFive(5);

lambda(x)->{x - 1;}(5);

function apply(f, x){
	f(x);
}

function square(x){
	x*x;
}

apply(square, 4);

function nested(x){
	lambda(y)->{
		lambda(z)->{
			z+x+y;
		};
	};
}

((nested(1))(2))(3);

//(5)();

"first" + "second";

function println(s){
	print(s, "\n");
}

//print(5, "\n");

println([1, 3, 5]);

println(array(5));

println(square(5));

println(square(4));

function manyparam(a, b, c, d, e){
	a + b + c + d + e;
}

println(manyparam(1, 2, 3, 4, 5));

println(18 - 6 * 2);

var n = "not null";

println(n);

n = null;

println(n);

if(null == n){
	println("Null checks work");
}

if(null == 5){
	println("this should not execute");
}
else{
	println("we good");
}

function book(book_title){
	var title = book_title;

	function setTitle(newTitle){
		title = newTitle;
	}

	this;
}

var mybook = book("Of Mice and Men");

println(mybook.title);

mybook.setTitle("Moby Dick");

println(mybook.title);

println("before textbook");

class textbook(book_title){
	var title = book_title;

	function setTitle(newTitle){
		title = newTitle;
	}
}

println(textbook);

var CS_textbook = textbook("Structure and Interpretation of Computer Programs");

CS_textbook.setTitle("Introduction to Algorithms");

println(CS_textbook.title);

var sizeTest = [];

println(size(sizeTest));

var x = "test string";

var y = "test string";

var z = x;

print("x == y ", (x == y), " (should be true)\n");
print("x is y ", (x is y), " (should be false)\n");
print("x == z ", (x == z), " (should be true)\n");
print("x is z ", (x is z), " (should be true)\n");
