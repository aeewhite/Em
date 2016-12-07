function println(s){print(s, "\n");}

function apply(f, x){
    f(x);
}

function square(x){
    x*x;
}

println(apply(square, 4));

println(apply(lambda(x)->{x*x;}, 4));

var multiply = lambda(x, y)->{x * y;};

println(multiply(4, 5));

function adjuster(x){
	lambda(y)->{x+y;};
}

var plusFive = adjuster(5);
var plus10 = adjuster(10);

print(plusFive(4), "\n");
print(plus10(5), "\n");
print(plusFive(6), "\n");

